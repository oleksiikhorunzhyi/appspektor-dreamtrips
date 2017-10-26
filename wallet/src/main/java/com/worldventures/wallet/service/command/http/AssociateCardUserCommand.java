package com.worldventures.wallet.service.command.http;

import com.worldventures.core.janet.cache.CacheOptions;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.AssociateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.AssociationCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.AssociationUserData;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.ImmutableAssociationCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.ImmutableAssociationUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.wallet.domain.entity.SmartCardDetails;
import com.worldventures.wallet.domain.entity.TermsAndConditions;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.service.SystemPropertiesProvider;
import com.worldventures.wallet.util.WalletValidateHelper;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class AssociateCardUserCommand extends Command<SmartCardDetails> implements InjectableAction, CachedAction<SmartCardDetails> {

   @Inject Janet janet;
   @Inject WalletStorage sto;
   @Inject MapperyContext mapperyContext;
   @Inject SystemPropertiesProvider propertiesProvider;

   private final String barcode;
   private UpdateCardUserData updateCardUserData;

   public AssociateCardUserCommand(String barcode, UpdateCardUserData cardUserData) {
      this.barcode = barcode;
      this.updateCardUserData = cardUserData;
   }

   @Override
   protected void run(CommandCallback<SmartCardDetails> callback) throws Throwable {
      WalletValidateHelper.validateSCIdOrThrow(barcode);

      AssociationUserData userData = ImmutableAssociationUserData.builder()
            .firstName(updateCardUserData.firstName())
            .middleName(updateCardUserData.middleName())
            .lastName(updateCardUserData.lastName())
            .displayPhoto(updateCardUserData.photoUrl())
            .phone(updateCardUserData.phone())
            .build();

      AssociationCardUserData data = ImmutableAssociationCardUserData.builder()
            .scid(Long.parseLong(barcode))
            .deviceModel(propertiesProvider.deviceName())
            .deviceOsVersion(propertiesProvider.osVersion())
            .deviceId(propertiesProvider.deviceId())
            .acceptedTermsAndConditionVersion(obtainTACVersion())
            .user(userData)
            .build();

      janet.createPipe(AssociateCardUserHttpAction.class)
            .createObservableResult(new AssociateCardUserHttpAction(data))
            .map(action -> mapperyContext.convert(action.response(), SmartCardDetails.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private int obtainTACVersion() {
      TermsAndConditions termsAndConditions = sto.getWalletTermsAndConditions();
      if (termsAndConditions == null) {
         throw new IllegalArgumentException("You don't have Terms and Conditions data in DB");
      }

      return Integer.parseInt(termsAndConditions.tacVersion());
   }

   @Override
   public SmartCardDetails getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, SmartCardDetails cache) {
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .saveToCache(true)
            .restoreFromCache(false)
            .build();
   }
}

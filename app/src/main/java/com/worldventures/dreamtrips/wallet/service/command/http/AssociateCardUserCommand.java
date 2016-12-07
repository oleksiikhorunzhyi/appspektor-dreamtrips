package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.user_association.AssociateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.AssociationCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.ImmutableAssociationCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;
import com.worldventures.dreamtrips.wallet.domain.entity.TermsAndConditions;
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class AssociateCardUserCommand extends Command<SmartCardDetails> implements InjectableAction, CachedAction<SmartCardDetails> {

   @Inject @Named(JANET_API_LIB) Janet janet;
   @Inject SnappyRepository repository;
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

      AssociationCardUserData data = ImmutableAssociationCardUserData.builder()
            .scid(Long.parseLong(barcode))
            .deviceModel(propertiesProvider.deviceName())
            .deviceOsVersion(propertiesProvider.osVersion())
            .deviceId(propertiesProvider.deviceId())
            .acceptedTermsAndConditionVersion(obtainTACVersion())
            .displayFirstName(updateCardUserData.displayFirstName())
            .displayLastName(updateCardUserData.displayLastName())
            .displayMiddleName(updateCardUserData.displayMiddleName())
            .displayPhoto(updateCardUserData.photoUrl())
            .build();

      janet.createPipe(AssociateCardUserHttpAction.class)
            .createObservableResult(new AssociateCardUserHttpAction(data))
            .map(action -> mapperyContext.convert(action.response(), SmartCardDetails.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private int obtainTACVersion() {
      TermsAndConditions termsAndConditions = repository.getWalletTermsAndConditions();
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

package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.user_association.AssociateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.AssociationCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.ImmutableAssociationCardUserData;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;
import com.worldventures.dreamtrips.wallet.domain.entity.TermsAndConditions;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardDetails;
import com.worldventures.dreamtrips.wallet.util.SmartphoneUtils;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class AssociateCardUserCommand extends Command<SmartCardDetails> implements InjectableAction, CachedAction<SmartCardDetails> {

   @Inject @Named(JANET_API_LIB) Janet janet;
   @Inject SnappyRepository repository;

   private final String barcode;

   public AssociateCardUserCommand(String barcode) {
      this.barcode = barcode;
   }

   @Override
   protected void run(CommandCallback<SmartCardDetails> callback) throws Throwable {
      WalletValidateHelper.validateSCIdOrThrow(barcode);

      AssociationCardUserData data = ImmutableAssociationCardUserData.builder()
            .scid(Long.parseLong(barcode))
            .deviceModel(SmartphoneUtils.getDeviceName())
            .deviceOsVersion(SmartphoneUtils.getOsVersion())
            .acceptedTermsAndConditionVersion(obtainTACVersion())
            .build();

      janet.createPipe(AssociateCardUserHttpAction.class)
            .createObservableResult(new AssociateCardUserHttpAction(data))
            .map(action -> convertResponse(action.response()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private int obtainTACVersion() {
      TermsAndConditions termsAndConditions = repository.getWalletTermsAndConditions();
      if (termsAndConditions == null) {
         throw new IllegalArgumentException("You don't have Terms and Conditions data in DB");
      }

      return Integer.parseInt(termsAndConditions.tacVersion());
   }

   private SmartCardDetails convertResponse(com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardDetails details) {
      return ImmutableSmartCardDetails.builder()
            //todo replace it in future. For now server returns stub values
            .smartCardId(details.scID())
            .bleAddress("DA:30:55:CF:B4:9E") // dev board address
            .nxtOrderId(details.nxtOrderId())
            .orderDate(details.orderDate())
            .wvOrderId(details.wvOrderId())
            .serialNumber(details.serialNumber())
            .revVersion(details.revVersion())
            .build();
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

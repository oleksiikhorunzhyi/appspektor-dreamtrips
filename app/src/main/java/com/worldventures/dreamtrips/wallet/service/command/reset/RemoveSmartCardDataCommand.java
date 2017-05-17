package com.worldventures.dreamtrips.wallet.service.command.reset;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import timber.log.Timber;

@CommandAction
public class RemoveSmartCardDataCommand extends Command<Void> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;
   @Inject RecordsStorage recordsStorage;
   @Inject LostCardRepository lostCardRepository;

   private final ResetOptions factoryResetOptions;

   public RemoveSmartCardDataCommand(ResetOptions factoryResetOptions) {
      this.factoryResetOptions = factoryResetOptions;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (factoryResetOptions.isWithPaymentCards()) deletePaymentsData();
      if (factoryResetOptions.isWithUserSmartCardData()) deleteUserData();
      snappyRepository.deleteSmartCardFirmware();
      snappyRepository.deleteSmartCardDetails();
      snappyRepository.deleteSmartCard();
      snappyRepository.deleteTermsAndConditions();
      snappyRepository.deletePinOptionChoice();
      lostCardRepository.clear();
      callback.onSuccess(null);
   }

   private void deletePaymentsData() {
      recordsStorage.deleteAllRecords();
      recordsStorage.deleteDefaultRecordId();
   }

   private void deleteUserData() {
      final SmartCardUser smartCardUser = snappyRepository.getSmartCardUser();
      clearUserImageCache(smartCardUser.userPhoto());
      snappyRepository.deleteSmartCardUser();
   }


   private void clearUserImageCache(SmartCardUserPhoto photo) {
      try {
         Fresco.getImagePipeline().evictFromCache(Uri.parse(photo.photoUrl()));
      } catch (Exception e) {
         Timber.e(e, "");
      }
   }
}

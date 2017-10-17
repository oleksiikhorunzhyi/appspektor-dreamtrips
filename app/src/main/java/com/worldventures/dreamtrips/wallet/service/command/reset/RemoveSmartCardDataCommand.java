package com.worldventures.dreamtrips.wallet.service.command.reset;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;
import com.worldventures.dreamtrips.wallet.util.CachedPhotoUtil;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RemoveSmartCardDataCommand extends Command<Void> implements InjectableAction {

   @Inject WalletStorage walletStorage;
   @Inject RecordsStorage recordsStorage;
   @Inject LostCardRepository lostCardRepository;
   @Inject CachedPhotoUtil cachedPhotoUtil;

   private final ResetOptions factoryResetOptions;

   public RemoveSmartCardDataCommand(ResetOptions factoryResetOptions) {
      this.factoryResetOptions = factoryResetOptions;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (factoryResetOptions.isWithPaymentCards()) deletePaymentsData();
      if (factoryResetOptions.isWithUserSmartCardData()) deleteUserData();
      walletStorage.deleteSmartCardFirmware();
      walletStorage.deleteSmartCardDetails();
      walletStorage.deleteSmartCard();
      walletStorage.deleteTermsAndConditions();
      walletStorage.deletePinOptionChoice();
      walletStorage.deleteSmartCardDisplayType();
      lostCardRepository.clear();
      callback.onSuccess(null);
   }

   private void deletePaymentsData() {
      recordsStorage.deleteAllRecords();
      recordsStorage.deleteDefaultRecordId();
   }

   private void deleteUserData() {
      final SmartCardUser smartCardUser = walletStorage.getSmartCardUser();
      clearUserImageCache(smartCardUser.userPhoto());
      walletStorage.deleteSmartCardUser();
   }


   private void clearUserImageCache(SmartCardUserPhoto photo) {
      if (photo != null) {
         cachedPhotoUtil.removeCachedPhoto(photo.uri());
      }
   }
}

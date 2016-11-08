package com.worldventures.dreamtrips.wallet.ui.settings.firmware.uptodate;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class WalletUpToDateFirmwarePresenter extends WalletPresenter<WalletUpToDateFirmwarePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;

   public WalletUpToDateFirmwarePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeSmartCard();
   }

   private void observeSmartCard() {
      smartCardInteractor.smartCardModifierPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bindSmartCard(command.getResult()));
   }

   private void bindSmartCard(SmartCard smartCard) {
      getView().version(smartCard.firmWareVersion());
   }

   void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void version(String version);
   }
}
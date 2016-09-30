package com.worldventures.dreamtrips.wallet.ui.settings.firmware.installsuccess;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.CardListPath;

import javax.inject.Inject;

public class WalletSuccessInstallFirmwarePresenter extends WalletPresenter<WalletSuccessInstallFirmwarePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;

   public WalletSuccessInstallFirmwarePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(it -> {
               getView().setSubTitle(it.getResult().firmWareVersion());
            }, e -> {/*todo correct error handling*/ });
   }

   void goDashboard() {
      navigator.single(new CardListPath());
   }

   public interface Screen extends WalletScreen {

      void setSubTitle(String version);
   }
}
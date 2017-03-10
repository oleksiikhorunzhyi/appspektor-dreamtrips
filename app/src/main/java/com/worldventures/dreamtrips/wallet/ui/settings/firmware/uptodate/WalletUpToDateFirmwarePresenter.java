package com.worldventures.dreamtrips.wallet.ui.settings.firmware.uptodate;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.ViewSdkVersionAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class WalletUpToDateFirmwarePresenter extends WalletPresenter<WalletUpToDateFirmwarePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public WalletUpToDateFirmwarePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeSmartCard();
      sendAnalyticViewAction();
   }

   private void observeSmartCard() {
      smartCardInteractor.smartCardFirmwarePipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bindSmartCardFirmware(command.getResult()));
   }

   private void sendAnalyticViewAction() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new ViewSdkVersionAction()));
   }

   private void bindSmartCardFirmware(SmartCardFirmware smartCardFirmware) {
      getView().version(smartCardFirmware);
   }

   void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void version(@Nullable SmartCardFirmware version);
   }
}

package com.worldventures.dreamtrips.wallet.ui.settings.firmware.puck_connection;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.donwload.WalletDownloadFirmwarePath;

import javax.inject.Inject;

public class WalletPuckConnectionPresenter extends WalletPresenter<WalletPuckConnectionPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   private final FirmwareInfo firmwareInfo;
   private final String firmwarePath;

   public WalletPuckConnectionPresenter(Context context, Injector injector, FirmwareInfo firmwareInfo, String firmwarePath) {
      super(context, injector);
      this.firmwareInfo = firmwareInfo;
      this.firmwarePath = firmwarePath;
   }

   void goNext() {
      navigator.withoutLast(new WalletDownloadFirmwarePath(firmwareInfo, firmwarePath));
   }

   void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

   }

}

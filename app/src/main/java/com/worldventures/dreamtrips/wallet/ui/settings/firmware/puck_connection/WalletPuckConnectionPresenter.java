package com.worldventures.dreamtrips.wallet.ui.settings.firmware.puck_connection;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.donwload.WalletDownloadFirmwarePath;

import javax.inject.Inject;

public class WalletPuckConnectionPresenter extends WalletPresenter<WalletPuckConnectionPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   public WalletPuckConnectionPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   void goNext() {
      navigator.withoutLast(new WalletDownloadFirmwarePath());
   }

   void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

   }

}

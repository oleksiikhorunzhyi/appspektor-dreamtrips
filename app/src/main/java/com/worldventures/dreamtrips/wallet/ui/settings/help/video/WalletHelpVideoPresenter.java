package com.worldventures.dreamtrips.wallet.ui.settings.help.video;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public class WalletHelpVideoPresenter extends WalletPresenter<WalletHelpVideoPresenter.Screen, Parcelable> {

   public WalletHelpVideoPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   public interface Screen extends WalletScreen {

   }
}

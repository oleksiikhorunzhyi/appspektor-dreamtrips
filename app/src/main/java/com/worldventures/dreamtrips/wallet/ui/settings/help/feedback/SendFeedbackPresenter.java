package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class SendFeedbackPresenter extends WalletPresenter<SendFeedbackPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   public SendFeedbackPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   public interface Screen extends WalletScreen {

   }
}

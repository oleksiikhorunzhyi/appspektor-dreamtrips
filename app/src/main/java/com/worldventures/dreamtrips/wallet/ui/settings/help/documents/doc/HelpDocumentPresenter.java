package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc;


import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class HelpDocumentPresenter extends WalletPresenter<HelpDocumentPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   public HelpDocumentPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().showDocument();
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
      void showDocument();
   }
}

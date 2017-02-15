package com.worldventures.dreamtrips.wallet.ui.settings.newcard.check;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class PreCheckNewCardPresenter extends WalletPresenter<PreCheckNewCardPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   public PreCheckNewCardPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
   }

   void prepareContinueAddCard() {
      // TODO: 2/16/17 add fetch of scID and show continue dialog
   }

   void navigateNext() {

   }

   void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
      void showAddCardContinueDialog(String scId);
   }
}

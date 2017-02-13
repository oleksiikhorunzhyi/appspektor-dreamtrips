package com.worldventures.dreamtrips.wallet.ui.settings.newcard.unassign;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.success.UnassignSuccessPath;

import javax.inject.Inject;

public class ExistingCardDetectPresenter extends WalletPresenter<ExistingCardDetectPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   public ExistingCardDetectPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
   }

   public void unassignCard() {
      // TODO: 2/14/17 add logic for unassign card
      navigator.single(new UnassignSuccessPath()); //test
   }

   public void prepareUnassignCard() {
      // TODO: 2/14/17 add get scID from data
      String scId = "121"; //test
      getView().showConfirmationUnassignDialog(scId);
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void setSmartCardId(String scId);

      void showViewForSCConnected();

      void showViewForSCDisconnected();

      void showConfirmationUnassignDialog(String scId);
   }
}

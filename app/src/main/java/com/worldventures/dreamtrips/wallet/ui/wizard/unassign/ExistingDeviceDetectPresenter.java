package com.worldventures.dreamtrips.wallet.ui.wizard.unassign;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.helper.CardIdUtil;

import javax.inject.Inject;

public class ExistingDeviceDetectPresenter extends WalletPresenter<ExistingDeviceDetectPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   public ExistingDeviceDetectPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   private void bindSmartCardId(String smartCardId) {
      getView().setSmartCardId(CardIdUtil.pushZeroToSmartCardId(smartCardId));
   }

   void unpair() {
      // TODO: 4/3/17 for test
      getView().showConfirmDialog("68240988");
   }

   void unpairConfirmed() {

   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void setSmartCardId(String scId);

      void showConfirmDialog(String scId);
   }
}

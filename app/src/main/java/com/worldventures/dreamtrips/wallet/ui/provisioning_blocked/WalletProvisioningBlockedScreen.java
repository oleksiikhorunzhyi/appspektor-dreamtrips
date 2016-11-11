package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import java.util.List;


public class WalletProvisioningBlockedScreen extends WalletLinearLayout<WalletProvisioningBlockedPresenter.Screen, WalletProvisioningBlockedPresenter, WalletProvisioningBlockedPath> implements WalletProvisioningBlockedPresenter.Screen {

   public WalletProvisioningBlockedScreen(Context context) {
      super(context);
   }

   public WalletProvisioningBlockedScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected boolean hasToolbar() {
      return false;
   }

   @NonNull
   @Override
   public WalletProvisioningBlockedPresenter createPresenter() {
      return new WalletProvisioningBlockedPresenter(getContext(), getInjector());
   }


   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void onSupportedDevicesLoaded(List<String> devices) {

   }
}

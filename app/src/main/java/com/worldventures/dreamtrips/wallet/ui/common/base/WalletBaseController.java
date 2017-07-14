package com.worldventures.dreamtrips.wallet.ui.common.base;


import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import butterknife.ButterKnife;

public abstract class WalletBaseController<V extends WalletScreen, P extends WalletPresenterI> extends PresentableController<V, P> implements WalletScreen {

   private Injector injector;
   private WalletScreenDelegate walletScreenDelegate;

   public WalletBaseController() {
   }

   @Override
   public View createView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      final View view = inflateView(layoutInflater, viewGroup);
      onFinishInflate(view);
      return view;
   }

   protected void onFinishInflate(View view) {
      ButterKnife.inject(view);
      this.injector = (Injector) getApplicationContext();
      injector.inject(this);
      this.walletScreenDelegate = WalletScreenDelegate.create(view, supportConnectionStatusLabel(), supportHttpConnectionStatusLabel());
   }

   @Override
   public void showConnectionStatus(ConnectionStatus connectionStatus) {
      walletScreenDelegate.showConnectionStatus(connectionStatus);
   }

   @Override
   public void showHttpConnectionStatus(boolean connected) {
      walletScreenDelegate.showHttpConnectionStatus(connected);
   }

   public Injector getInjector() {
      return injector;
   }

   protected String getString(@StringRes int stringId) {
      return getResources().getString(stringId);
   }

   protected String getString(@StringRes int stringId, Object... formatArgs) {
      return getResources().getString(stringId, formatArgs);
   }

   public abstract View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup);

   public abstract boolean supportConnectionStatusLabel();

   public abstract boolean supportHttpConnectionStatusLabel();
}

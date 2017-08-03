package com.worldventures.dreamtrips.wallet.ui.common.base;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.techery.spares.ui.activity.InjectingActivity;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import butterknife.ButterKnife;
import dagger.ObjectGraph;

import static android.content.Context.INPUT_METHOD_SERVICE;

public abstract class WalletBaseController<V extends WalletScreen, P extends WalletPresenter> extends PresentableController<V, P> implements WalletScreen {
   private ObjectGraph objectGraph;
   private WalletScreenDelegate walletScreenDelegate;

   public WalletBaseController() {
   }

   public WalletBaseController(Bundle args) {
      super(args);
   }

   @Override
   public View createView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      final View view = inflateView(layoutInflater, viewGroup);
      onFinishInflate(view);
      return view;
   }

   protected void onFinishInflate(View view) {
      ButterKnife.inject(this, view);
      //noinspection all
      this.objectGraph = (ObjectGraph) view.getContext().getSystemService(InjectingActivity.OBJECT_GRAPH_SERVICE_NAME);
      objectGraph.inject(this);
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

   protected String getString(@StringRes int stringId) {
      return getResources().getString(stringId);
   }

   protected String getString(@StringRes int stringId, Object... formatArgs) {
      return getResources().getString(stringId, formatArgs);
   }

   @Override
   protected void onDetach(@NonNull View view) {
      super.onDetach(view);
      final InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE);
      inputManager.hideSoftInputFromWindow(null, 0);
   }

   public abstract View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup);

   public abstract boolean supportConnectionStatusLabel();

   public abstract boolean supportHttpConnectionStatusLabel();
}

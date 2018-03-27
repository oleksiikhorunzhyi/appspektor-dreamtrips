package com.worldventures.wallet.ui.common.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.worldventures.core.janet.Injector;
import com.worldventures.wallet.domain.entity.ConnectionStatus;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import dagger.ObjectGraph;
import rx.Observable;
import rx.subjects.PublishSubject;

import static android.content.Context.INPUT_METHOD_SERVICE;

public abstract class WalletBaseController<V extends WalletScreen, P extends WalletPresenter> extends PresentableController<V, P> implements WalletScreen {

   private final PublishSubject<Void> detachStopper = PublishSubject.create();
   private WalletScreenDelegate walletScreenDelegate;

   public WalletBaseController() {
      // no-args controller, should be still safe for conductor
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

   @SuppressWarnings("ResourceType")
   protected void onFinishInflate(View view) {
      final Object module = screenModule();
      ObjectGraph objectGraph = (ObjectGraph) view.getContext().getSystemService(Injector.OBJECT_GRAPH_SERVICE_NAME);
      if (module != null) {
         objectGraph = objectGraph.plus(module);
      }
      objectGraph.inject(this);
      this.walletScreenDelegate = WalletScreenDelegate.create(view, supportConnectionStatusLabel(), supportHttpConnectionStatusLabel());
   }

   @Nullable
   protected Object screenModule() {
      return null;
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
      detachStopper.onNext(null);
      super.onDetach(view);
      final InputMethodManager inputManager = (InputMethodManager) view.getContext()
            .getSystemService(INPUT_METHOD_SERVICE);
      inputManager.hideSoftInputFromWindow(null, 0);
   }

   @Override
   public <T> Observable.Transformer<T, T> bindUntilDetach() {
      return input -> input.takeUntil(detachStopper);
   }

   public PublishSubject<Void> getDetachStopper() {
      return detachStopper;
   }

   public abstract View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup);

   public abstract boolean supportConnectionStatusLabel();

   public abstract boolean supportHttpConnectionStatusLabel();
}

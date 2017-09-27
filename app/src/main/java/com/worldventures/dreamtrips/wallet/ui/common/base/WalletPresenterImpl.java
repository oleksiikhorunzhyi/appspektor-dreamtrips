package com.worldventures.dreamtrips.wallet.ui.common.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class WalletPresenterImpl<V extends WalletScreen> extends MvpBasePresenter<V> implements WalletPresenter<V> {

   @SuppressWarnings("WeakerAccess")
   private final Navigator navigator;
   private final WalletDeviceConnectionDelegate deviceConnectionDelegate;

   private final PublishSubject<Void> detachStopper = PublishSubject.create();

   public WalletPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate) {
      this.navigator = navigator;
      this.deviceConnectionDelegate = deviceConnectionDelegate;
   }

   @Override
   public void attachView(V view) {
      super.attachView(view);
      deviceConnectionDelegate.setup(view);
   }

   @Override
   public void detachView(boolean retainInstance) {
      detachStopper.onNext(null);
      super.detachView(retainInstance);
   }

   public Navigator getNavigator() {
      return navigator;
   }

   protected <T> Observable.Transformer<T, T> bindView() {
      return input -> input.takeUntil(detachStopper);
   }

   protected <T> Observable.Transformer<T, T> bindViewIoToMainComposer() {
      return input -> input.compose(new IoToMainComposer<>()).compose(bindView());
   }
}

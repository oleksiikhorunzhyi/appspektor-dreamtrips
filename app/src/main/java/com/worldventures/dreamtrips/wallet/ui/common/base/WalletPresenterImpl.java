package com.worldventures.dreamtrips.wallet.ui.common.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class WalletPresenterImpl<V extends WalletScreen> extends MvpBasePresenter<V> implements WalletPresenterI<V> {

   @SuppressWarnings("WeakerAccess")
   private final NavigatorConductor navigator;
   private final SmartCardInteractor smartCardInteractor;
   private final WalletNetworkService networkService;

   private final PublishSubject<Void> detachStopper = PublishSubject.create();

   public WalletPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,  WalletNetworkService networkService) {
      this.navigator = navigator;
      this.smartCardInteractor = smartCardInteractor;
      this.networkService = networkService;
   }

   @Override
   public void attachView(V view) {
      super.attachView(view);
      observeSmartCardModifierPipe();
      observeHttpConnectionState();
   }

   private void observeHttpConnectionState() {
      networkService.observeConnectedState()
            .throttleLast(1, TimeUnit.SECONDS)
            .distinctUntilChanged()
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::showHttpConnectionStatus);
   }

   private void observeSmartCardModifierPipe() {
      smartCardInteractor.deviceStatePipe()
            .observeSuccessWithReplay()
            .throttleLast(1, TimeUnit.SECONDS)
            .map(command -> command.getResult().connectionStatus())
            .distinctUntilChanged()
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::showConnectionStatus);
      smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.fetch());
   }

   @Override
   public void detachView(boolean retainInstance) {
      detachStopper.onNext(null);
      super.detachView(retainInstance);
   }

   public SmartCardInteractor getSmartCardInteractor() {
      return smartCardInteractor;
   }

   public WalletNetworkService getNetworkService() {
      return networkService;
   }

   public NavigatorConductor getNavigator() {
      return navigator;
   }

   protected <T> Observable.Transformer<T, T> bindView() {
      return input -> input.takeUntil(detachStopper);
   }

   protected <T> Observable.Transformer<T, T> bindViewIoToMainComposer() {
      return input -> input.compose(new IoToMainComposer<>()).compose(bindView());
   }
}

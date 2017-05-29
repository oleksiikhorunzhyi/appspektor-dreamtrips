package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.content.Context;
import android.os.Parcelable;

import com.messenger.ui.presenter.BaseViewStateMvpPresenter;
import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public abstract class WalletPresenter<V extends WalletScreen, S extends Parcelable> extends BaseViewStateMvpPresenter<V, S> implements ViewStateMvpPresenter<V, S> {

   @SuppressWarnings("WeakerAccess")
   @Inject SmartCardInteractor interactor;
   @Inject WalletNetworkService networkService;

   private Context context;
   private Injector injector;

   public WalletPresenter(Context context, Injector injector) {
      this.context = context;
      this.injector = injector;
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
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
      interactor.deviceStatePipe()
            .observeSuccessWithReplay()
            .throttleLast(1, TimeUnit.SECONDS)
            .map(command -> command.getResult().connectionStatus())
            .distinctUntilChanged()
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::showConnectionStatus);
      interactor.deviceStatePipe().send(DeviceStateCommand.fetch());
   }

   public Context getContext() {
      return context;
   }

   public Injector getInjector() {
      return injector;
   }

   @Override
   public void onNewViewState() {
   }

   @Override
   public void applyViewState() {
   }
}

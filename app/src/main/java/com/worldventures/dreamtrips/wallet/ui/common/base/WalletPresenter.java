package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.content.Context;
import android.os.Parcelable;

import com.messenger.ui.presenter.BaseViewStateMvpPresenter;
import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import javax.inject.Inject;

public abstract class WalletPresenter<V extends WalletScreen, S extends Parcelable> extends BaseViewStateMvpPresenter<V, S> implements ViewStateMvpPresenter<V, S> {

   @Inject SmartCardInteractor smartCardInteractor;

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
   }

   private void observeSmartCardModifierPipe() {
      smartCardInteractor.smartCardModifierPipe()
            .observeSuccessWithReplay()
            .filter(command -> command.getResult() != null)
            .map(command -> command.getResult().connectionStatus())
            .startWith(smartCardInteractor.activeSmartCardPipe()
                  .createObservableResult(new GetActiveSmartCardCommand())
                  .onErrorReturn(throwable -> null).filter(it -> it != null)
                  .map(it -> it.getResult().connectionStatus())
            )
            .distinctUntilChanged()
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::showConnectionStatus);
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

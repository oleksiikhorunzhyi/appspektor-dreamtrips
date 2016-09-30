package com.worldventures.dreamtrips.wallet.ui.settings.factory_reset;


import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.factory_reset_success.FactoryResetSuccessPath;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class FactoryResetPresenter extends WalletPresenter<FactoryResetPresenter.Screen, Parcelable> {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject Navigator navigator;

   public FactoryResetPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      startUnassignUser();
   }

   private void startUnassignUser() {
      smartCardInteractor.resetSmartCardPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ResetSmartCardCommand>()
                  .onFail((action, throwable) -> getView().provideOperationDelegate()
                        .showError(getContext().getString(R.string.wallet_wizard_setup_error), null))
                  .onStart(action -> getView().provideOperationDelegate().showProgress())
                  .onSuccess(resetSmartCardCommand -> navigator.single(new FactoryResetSuccessPath())));

      smartCardInteractor.resetSmartCardPipe().send(new ResetSmartCardCommand());
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
   }

}

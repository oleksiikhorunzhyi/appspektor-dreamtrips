package com.worldventures.dreamtrips.wallet.ui.settings.factory_reset;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.FactoryResetManager;
import com.worldventures.dreamtrips.wallet.service.command.reset.ConfirmResetCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.factory_reset_success.FactoryResetSuccessPath;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class FactoryResetPresenter extends WalletPresenter<FactoryResetPresenter.Screen, Parcelable> {

   @Inject FactoryResetManager factoryResetManager;
   @Inject Navigator navigator;

   public FactoryResetPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      resetSmartCard();
   }

   private void resetSmartCard() {
      factoryResetManager.confirmResetPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ConfirmResetCommand>()
                  .onSuccess(confirmCommand -> factoryResetManager.observeFactoryResetPipe()
                        .compose(bindViewIoToMainComposer())
                        .subscribe(OperationActionStateSubscriberWrapper.<ResetSmartCardCommand>forView(getView().provideOperationDelegate())
                              .onSuccess(command -> navigator.single(new FactoryResetSuccessPath()))
                              .onFail(ErrorHandler.<ResetSmartCardCommand>builder(getContext())
                                    .defaultMessage(R.string.wallet_wizard_setup_error)
                                    .build())
                              .wrap()))
                  .onFail((confirmResetCommand, throwable) -> goBack())
            );

      factoryResetManager.factoryReset();
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
   }

}

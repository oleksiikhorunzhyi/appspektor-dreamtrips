package com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.TermsAcceptedAction;
import com.worldventures.dreamtrips.wallet.analytics.TermsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPath;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

public class WizardTermsPresenter extends WalletPresenter<WizardTermsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject @Named(JANET_WALLET) Janet janet;

   public WizardTermsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      loadTerms();
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new TermsAction()));
   }

   public void acceptTermsPressed() {
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new TermsAcceptedAction()));
      navigator.withoutLast(new WizardSplashPath());
   }

   protected void loadTerms() {
      janet.createPipe(FetchTermsAndConditionsCommand.class)
            .createObservable(new FetchTermsAndConditionsCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().termsOperationView())
                  .onSuccess(command -> getView().showTerms(command.getResult().url()))
                  .create());
   }

   public void onBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void showTerms(String url);

      OperationView<FetchTermsAndConditionsCommand> termsOperationView();
   }
}

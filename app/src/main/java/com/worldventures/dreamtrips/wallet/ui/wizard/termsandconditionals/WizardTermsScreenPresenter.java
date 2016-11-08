package com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.AcceptTermsAction;
import com.worldventures.dreamtrips.wallet.analytics.TermsAcceptedAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPath;

import javax.inject.Inject;
import javax.inject.Named;

import flow.Flow.Direction;
import io.techery.janet.Janet;
import rx.functions.Action1;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

public class WizardTermsScreenPresenter extends WalletPresenter<WizardTermsScreenPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject @Named(JANET_WALLET) Janet janet;

   public WizardTermsScreenPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      loadTerms();
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new AcceptTermsAction()));
   }

   public void acceptTermsPressed() {
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new TermsAcceptedAction()));
      navigator.single(new WizardSplashPath(true), Direction.BACKWARD);
   }

   protected void loadTerms() {
      janet.createPipe(FetchTermsAndConditionsCommand.class)
            .createObservableResult(new FetchTermsAndConditionsCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorSubscriberWrapper.<FetchTermsAndConditionsCommand>forView(getView().provideOperationDelegate())
                  .onNext(new Action1<FetchTermsAndConditionsCommand>() {
                     @Override
                     public void call(FetchTermsAndConditionsCommand command) {
                        WizardTermsScreenPresenter.this.getView().showTerms(command.getResult().url());
                     }
                  })
                  .onFail(ErrorHandler.create(getContext(), throwable -> getView().failedToLoadTerms()))
                  .wrap()
            );
   }

   public void onBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void showTerms(String url);

      void failedToLoadTerms();
   }
}

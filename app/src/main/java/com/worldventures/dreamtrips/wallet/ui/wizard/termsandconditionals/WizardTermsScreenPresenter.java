package com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPath;

import javax.inject.Inject;
import javax.inject.Named;

import flow.Flow.Direction;
import io.techery.janet.Janet;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

public class WizardTermsScreenPresenter extends WalletPresenter<WizardTermsScreenPresenter.Screen, Parcelable> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject @Named(JANET_WALLET) Janet janet;

   public WizardTermsScreenPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      loadTerms();
   }

   public void acceptTermsPressed() {
      navigator.single(new WizardSplashPath(true), Direction.BACKWARD);
   }

   protected void loadTerms() {
      janet.createPipe(FetchTermsAndConditionsCommand.class)
            .createObservableResult(new FetchTermsAndConditionsCommand())
            .compose(bindViewIoToMainComposer())
            .map(it -> it.getResult().url())
            .subscribe(it -> getView().showTerms(it), throwable -> getView().failedToLoadTerms());
   }

   public void onBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void showTerms(String url);

      void failedToLoadTerms();
   }
}

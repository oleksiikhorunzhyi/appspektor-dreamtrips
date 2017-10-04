package com.worldventures.dreamtrips.modules.common.presenter;

import com.worldventures.core.modules.auth.api.command.LogoutCommand;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.core.modules.infopages.StaticPageProvider;
import com.worldventures.dreamtrips.modules.common.command.AcceptTermsCommand;
import com.worldventures.dreamtrips.modules.common.delegate.LegalInteractor;
import com.worldventures.dreamtrips.modules.common.service.analytics.TermsAndConditionsAction;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class TermsConditionsDialogPresenter extends Presenter<TermsConditionsDialogPresenter.View> {

   @Inject StaticPageProvider provider;
   @Inject AuthInteractor authInteractor;
   @Inject LegalInteractor legalInteractor;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      loadContent();
   }

   public void loadContent() {
      view.loadContent(provider.getTermsOfServiceUrl());
   }

   public void acceptTerms(String text) {
      analyticsInteractor.analyticsActionPipe().send(new TermsAndConditionsAction(true));
      view.disableButtons();
      legalInteractor.termsPipe()
            .createObservable(new AcceptTermsCommand(text))
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<AcceptTermsCommand>().onSuccess(action -> view.dismissDialog())
                  .onFail((action, e) -> {
                     view.enableButtons();
                     handleError(action, e);
                  }));
   }

   public void denyTerms() {
      analyticsInteractor.analyticsActionPipe().send(new TermsAndConditionsAction(false));
      logout();
   }

   public void logout() {
      authInteractor.logoutPipe().send(new LogoutCommand());
      view.dismissDialog();
   }

   public interface View extends Presenter.View {

      void loadContent(String url);

      void dismissDialog();

      void inject(Object object);

      void enableButtons();

      void disableButtons();
   }
}

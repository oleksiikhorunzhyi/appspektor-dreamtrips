package com.worldventures.dreamtrips.modules.common.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.api.command.LogoutCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.AcceptTermsCommand;
import com.worldventures.dreamtrips.modules.common.delegate.LegalInteractor;
import com.worldventures.dreamtrips.modules.common.service.LogoutInteractor;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class TermsConditionsDialogPresenter extends Presenter<TermsConditionsDialogPresenter.View> {

   @Inject StaticPageProvider provider;
   @Inject LogoutInteractor logoutInteractor;
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
      TrackingHelper.termsConditionsAction(true);
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
      TrackingHelper.termsConditionsAction(false);
      logout();
   }

   public void logout() {
      logoutInteractor.logoutPipe().send(new LogoutCommand());
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

package com.worldventures.dreamtrips.modules.common.presenter;

import com.worldventures.core.modules.auth.api.command.LogoutCommand;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.core.modules.infopages.StaticPageProvider;
import com.worldventures.core.modules.legal.LegalInteractor;
import com.worldventures.core.modules.legal.command.AcceptTermsCommand;
import com.worldventures.core.modules.legal.command.GetDocumentByTypeCommand;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.terms_and_conditions.model.BaseDocumentBody;
import com.worldventures.dreamtrips.api.terms_and_conditions.model.DocumentBodyWithUrl;
import com.worldventures.dreamtrips.modules.common.service.analytics.TermsAndConditionsAction;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class TermsConditionsDialogPresenter extends Presenter<TermsConditionsDialogPresenter.View> {

   @Inject StaticPageProvider provider;
   @Inject AuthInteractor authInteractor;
   @Inject LegalInteractor legalInteractor;

   private DocumentBodyWithUrl documentBodyWithUrl;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      loadDocument();
   }

   private void loadDocument() {
      legalInteractor.getGetDocumentByTypePipe()
            .createObservable(new GetDocumentByTypeCommand(BaseDocumentBody.GENERAL_TERMS))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetDocumentByTypeCommand>()
                  .onSuccess(command -> {
                     documentBodyWithUrl = command.getResult();
                     view.loadContent(documentBodyWithUrl.url());
                  })
                  .onFail((command, throwable) -> {
                     view.informUser(R.string.error_failed_to_load_terms_and_conditions);
                     view.showRetryButton();
                  }));
   }

   public void retry() {
      if (documentBodyWithUrl != null) {
         view.loadContent(documentBodyWithUrl.url());
      } else {
         loadDocument();
      }
   }

   public void acceptTerms() {
      analyticsInteractor.analyticsActionPipe().send(new TermsAndConditionsAction(true));
      view.disableButtons();
      legalInteractor.getAcceptTermsPipe()
            .createObservable(new AcceptTermsCommand(documentBodyWithUrl.type(), documentBodyWithUrl.version()))
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

      void showRetryButton();
   }
}

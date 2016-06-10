package com.worldventures.dreamtrips.modules.common.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.api.AcceptTermsConditionsCommand;
import com.worldventures.dreamtrips.modules.common.view.util.LogoutDelegate;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;

import javax.inject.Inject;

public class TermsConditionsDialogPresenter extends Presenter<TermsConditionsDialogPresenter.View> {

    @Inject
    StaticPageProvider provider;
    @Inject
    SnappyRepository snappyRepository;
    @Inject
    LogoutDelegate logoutDelegate;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        logoutDelegate.setOnLogoutSuccessListener(view::dismissDialog);
        view.loadContent(provider.getTermsOfServiceUrl());
    }

    @Override
    public void dropView() {
        logoutDelegate.setOnLogoutSuccessListener(null);
        super.dropView();
    }

    public void acceptTerms(String text) {
        TrackingHelper.termsConditionsAction(true);

        view.disableButtons();
        doRequest(new AcceptTermsConditionsCommand(text), aVoid -> {
            UserSession userSession = appSessionHolder.get().get();
            userSession.getUser().setTermsAccepted(true);
            appSessionHolder.put(userSession);
            view.dismissDialog();
        });
    }

    public void denyTerms() {
        TrackingHelper.termsConditionsAction(false);

        logout();
    }

    public void logout() {
        view.disableButtons();
        logoutDelegate.logout();
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.enableButtons();
    }

    public interface View extends Presenter.View {
        void loadContent(String url);

        void dismissDialog();

        void inject(Object object);

        void enableButtons();

        void disableButtons();
    }
}

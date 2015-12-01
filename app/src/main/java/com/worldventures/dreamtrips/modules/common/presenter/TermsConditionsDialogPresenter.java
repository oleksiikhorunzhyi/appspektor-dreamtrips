package com.worldventures.dreamtrips.modules.common.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.api.AcceptTermsConditionsCommand;
import com.worldventures.dreamtrips.modules.common.view.util.LogoutDelegate;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;

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
        view.loadContent(provider.getStaticInfoUrl(StaticInfoFragment.TERMS_TITLE));
    }

    @Override
    public void onInjected() {
        super.onInjected();
        logoutDelegate.setDreamSpiceManager(dreamSpiceManager);
    }

    public void acceptTerms(String text) {
        doRequest(new AcceptTermsConditionsCommand(text), aVoid -> view.dismissDialog());
    }

    public void logout() {
        logoutDelegate.logout();
    }

    public interface View extends Presenter.View {
        void loadContent(String url);

        void dismissDialog();

        void inject(Object object);
    }
}

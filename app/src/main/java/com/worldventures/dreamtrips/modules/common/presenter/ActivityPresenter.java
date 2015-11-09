package com.worldventures.dreamtrips.modules.common.presenter;

import android.app.Activity;
import android.content.res.Configuration;

import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.LocaleSwitcher;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.modules.common.model.User;

import javax.inject.Inject;

public class ActivityPresenter<VT extends ActivityPresenter.View> extends Presenter<VT> {

    @Inject
    protected Activity activity;
    @Inject
    protected LocaleSwitcher localeSwitcher;
    @Inject
    protected LocaleHelper localeHelper;

    @Override
    public void onInjected() {
        super.onInjected();
        setupUserLocale();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Some third-party libraries can change the locale.
        setupUserLocale();
    }

    @Override
    public void dropView() {
        super.dropView();
        activity = null;
    }

    public void onConfigurationChanged(Configuration configuration) {
        localeSwitcher.onConfigurationLocaleChanged(configuration.locale);
    }

    public void onEventMainThread(UpdateUserInfoEvent event) {
        if (event.user == null || event.user.isTermsAccepted() || !canShowTermsDialog()) return;
        //
        view.showTermsDialog();
        eventBus.removeStickyEvent(event);
    }

    protected boolean canShowTermsDialog() {
        return true;
    }

    public interface View extends Presenter.View {
        void showTermsDialog();
    }

    protected void setupUserLocale() {
        Optional<UserSession> userSession = appSessionHolder.get();
        if (!userSession.isPresent()) {
            localeSwitcher.resetLocale();
        } else {
            User user = userSession.get().getUser();
            if (user != null && user.getLocale() != null) {
                localeSwitcher.applyLocale(localeHelper.getAccountLocale(user));
            } else {
                localeSwitcher.resetLocale();
            }
        }
    }
}

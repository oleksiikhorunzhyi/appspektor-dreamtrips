package com.worldventures.dreamtrips.modules.common.presenter;

import android.app.Activity;
import android.content.res.Configuration;

import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.LocaleSwitcher;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.LogoutDelegate;

import javax.inject.Inject;

import icepick.State;

public class ActivityPresenter<VT extends ActivityPresenter.View> extends Presenter<VT> {

    @Inject protected Activity activity;
    @Inject protected LocaleSwitcher localeSwitcher;
    @Inject protected LocaleHelper localeHelper;
    @Inject protected LogoutDelegate logoutDelegate;

    @State boolean isTermsShown;

    @Override
    public void onInjected() {
        super.onInjected();
        setupUserLocale();
    }

    @Override
    public void takeView(VT view) {
        super.takeView(view);
        checkTermsAndConditionFromHolder();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Some third-party libraries can change the locale.
        setupUserLocale();
    }

    public void logout() {
        logoutDelegate.logout();
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
        if (checkTermsAndConditions(event.user)) return;
        eventBus.removeStickyEvent(event);
    }

    private void checkTermsAndConditionFromHolder() {
        Optional<UserSession> userSession = appSessionHolder.get();
        if (userSession.isPresent()) {
            checkTermsAndConditions(userSession.get().getUser());
        }
    }

    private boolean checkTermsAndConditions(User user) {
        if (user == null || user.isTermsAccepted() || !canShowTermsDialog()) return true;
        isTermsShown = true;
        view.showTermsDialog();
        return false;
    }

    protected boolean canShowTermsDialog() {
        return !activity.isFinishing() && !isTermsShown;
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

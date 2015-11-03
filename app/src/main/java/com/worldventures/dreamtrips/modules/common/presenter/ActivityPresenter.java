package com.worldventures.dreamtrips.modules.common.presenter;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.Log;

import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleManager;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.Locale;

import javax.inject.Inject;

public class ActivityPresenter<VT extends Presenter.View> extends Presenter<VT> {

    @Inject
    protected Activity activity;
    @Inject
    protected LocaleManager localeManager;

    @Override
    public void onInjected() {
        super.onInjected();

        Optional<UserSession> userSession = appSessionHolder.get();
        if (!userSession.isPresent()) {
            localeManager.resetLocale();
            return;
        }

        User user = userSession.get().getUser();
        if (user != null && user.getLocation() != null) {
            localeManager.setLocale(user.getLocation());
        } else {
            localeManager.resetLocale();
        }
    }

    @Override
    public void dropView() {
        super.dropView();
        activity = null;
    }

    public void onConfigurationChanged(Configuration configuration){
        localeManager.localeChanged(configuration.locale);
    }

}

package com.worldventures.dreamtrips.modules.infopages.presenter;

import android.webkit.WebViewClient;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.Session;

import java.util.concurrent.TimeUnit;

import rx.functions.Action0;
import timber.log.Timber;

public class AuthorizedStaticInfoPresenter extends WebViewFragmentPresenter<AuthorizedStaticInfoPresenter.View> {

    public static final int LIFE_DURATION = 30; // mins

    public AuthorizedStaticInfoPresenter(String url) {
        super(url);
    }

    @Override
    public void load() {
        doWithAuth(super::load);
    }

    @Override
    protected void reload() {
        doWithAuth(super::reload);
    }

    private void doWithAuth(Action0 action) {
        UserSession userSession = appSessionHolder.get().get();
        if (userSession.getLastUpdate() > System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(LIFE_DURATION)) {
            action.call();
        } else {
            view.setRefreshing(true);
            dreamSpiceManager.login(new RequestListener<Session>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Timber.e(spiceException, "Can't login during WebView loading");
                    view.showError(WebViewClient.ERROR_AUTHENTICATION);
                    view.setRefreshing(false);
                }

                @Override
                public void onRequestSuccess(Session o) {
                    view.setRefreshing(false);
                    reload();
                }
            });
        }
    }

    public interface View extends WebViewFragmentPresenter.View {
    }
}

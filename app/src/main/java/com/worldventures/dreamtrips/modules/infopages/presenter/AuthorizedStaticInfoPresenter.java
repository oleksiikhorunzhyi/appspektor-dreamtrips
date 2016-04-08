package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class AuthorizedStaticInfoPresenter extends WebViewFragmentPresenter<AuthorizedStaticInfoPresenter.View> {

    public static final int LIFE_DURATION = 30; // mins

    public AuthorizedStaticInfoPresenter(String url) {
        super(url);
    }

    @Override
    public void load() {
        UserSession userSession = appSessionHolder.get().get();
        if (userSession.getLastUpdate() > System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(LIFE_DURATION)) {
            super.load();
        } else {
            dreamSpiceManager.login(new RequestListener() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Timber.e(spiceException, "Can't login during WebView loading");
                }

                @Override
                public void onRequestSuccess(Object o) {
                    reload();
                }
            });
        }
    }

    public interface View extends WebViewFragmentPresenter.View {
    }

}

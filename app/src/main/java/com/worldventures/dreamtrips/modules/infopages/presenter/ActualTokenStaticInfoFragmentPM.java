package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class ActualTokenStaticInfoFragmentPM extends WebViewFragmentPresenter<ActualTokenStaticInfoFragmentPM.View> {
    public static final int LIFE_DURATION = 30;

    public ActualTokenStaticInfoFragmentPM(View view) {
        super(view);
    }


    public void loadUrl() {
        UserSession userSession = appSessionHolder.get().get();
        if (userSession.getLastUpdate() > System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(LIFE_DURATION)) {
            view.loadContent();
        } else {
            dreamSpiceManager.login(new RequestListener() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Timber.e(spiceException, "");
                }

                @Override
                public void onRequestSuccess(Object o) {
                    view.loadContent();
                }
            });
        }
    }

    public static interface View extends WebViewFragmentPresenter.View {
        void loadContent();
    }

}

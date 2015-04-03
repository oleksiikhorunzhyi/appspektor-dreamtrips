package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.trips.api.GetTripDetailsQuery;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class BookItActivityPresenter extends Presenter<BookItActivityPresenter.View> {

    public static final int LIFE_DURATION = 30;
    private static final String URL_BASE = "/trips/details/%s?user=%s&token=%s&appMode=true#/book";

    public BookItActivityPresenter(BookItActivityPresenter.View view) {
        super(view);
    }

    public void onCreate() {
        UserSession userSession = appSessionHolder.get().get();

        if (userSession.getLastUpdate() > System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(LIFE_DURATION)) {
            openBookIt();
        } else {
            dreamSpiceManager.execute(new GetTripDetailsQuery(view.getTripId()), new RequestListener<TripDetails>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Timber.e(spiceException, "");
                }

                @Override
                public void onRequestSuccess(TripDetails tripDetails) {
                    openBookIt();
                }
            });
        }
    }

    private void openBookIt() {
        UserSession userSession = appSessionHolder.get().get();
        AppConfig.URLS urls = userSession.getGlobalConfig().getUrls();
        AppConfig.URLS.Config config = BuildConfig.DEBUG ? urls.getProduction() : urls.getQA();
        String urlPrefix = config.getBookingPageBaseURL();
        String url = String.format(urlPrefix + URL_BASE, view.getTripId(), userSession.getUser().getUsername(),
                userSession.getLegacyApiToken());
        Bundle bundle = new Bundle();
        bundle.putString(StaticInfoFragment.BundleUrlFragment.URL_EXTRA, url);
        fragmentCompass.add(Route.BOOK_IT, bundle);
    }

    public static interface View extends Presenter.View {
        String getTripId();
    }
}
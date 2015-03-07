package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.TripDetails;
import com.worldventures.dreamtrips.core.model.config.URLS;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.view.fragment.StaticInfoFragment;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by Edward on 23.01.15.
 */
public class BookItActivityPresentation extends BasePresentation<BookItActivityPresentation.View> {

    private static final String URL_BASE = "/trips/details/%d?user=%s&token=%s&appMode=true#/book";
    public static final int LIFE_DURATION = 30;

    @Inject
    DreamTripsApi dreamTripsApi;

    public BookItActivityPresentation(BookItActivityPresentation.View view) {
        super(view);
    }


    public void onCreate() {
        UserSession userSession = appSessionHolder.get().get();
        if (userSession.getLastUpdate() > System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(LIFE_DURATION)) {
            openBookIt();
        } else {
            dreamTripsApi.getDetails(view.getTripId(), new Callback<TripDetails>() {
                @Override
                public void success(TripDetails tripDetails, Response response) {
                    openBookIt();
                }

                @Override
                public void failure(RetrofitError error) {
                    Timber.e(error, "");
                }
            });
        }

    }

    private void openBookIt() {
        UserSession userSession = appSessionHolder.get().get();
        URLS urls = userSession.getGlobalConfig().getUrls();
        URLS.Config config = BuildConfig.DEBUG ? urls.getProduction() : urls.getQA();
        String urlPrefix = config.getBookingPageBaseURL();
        String url = String.format(urlPrefix + URL_BASE, view.getTripId(), userSession.getUser().getUsername(),
                userSession.getLegacyApiToken());
        Bundle bundle = new Bundle();
        bundle.putString(StaticInfoFragment.BundleUrlFragment.URL_EXTRA, url);
        fragmentCompass.add(State.BOOK_IT, bundle);
    }

    public static interface View extends BasePresentation.View {
        int getTripId();
    }

}
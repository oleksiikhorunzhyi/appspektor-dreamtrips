package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.view.fragment.StaticInfoFragment;

import org.robobinding.annotation.PresentationModel;

/**
 * Created by Edward on 23.01.15.
 */
@PresentationModel
public class BookItActivityPresentation extends BasePresentation<BookItActivityPresentation.View> {

    private String urlBase = "https://www.dreamtrips.com/trips/details/%d?user=%s&token=%s&appMode=true#/book";

    public BookItActivityPresentation(BookItActivityPresentation.View view) {
        super(view);
    }

    public void onCreate() {
        String url = String.format(urlBase, view.getTripId(), appSessionHolder.get().get().getUser().getUsername(),
                appSessionHolder.get().get().getLegacyApiToken());
        Bundle bundle = new Bundle();
        bundle.putString(StaticInfoFragment.BookItFragment.URL_EXTRA, url);
        fragmentCompass.add(State.BOOK_IT, bundle);
    }

    public static interface View extends BasePresentation.View{
        int getTripId();
    }

}

package com.worldventures.dreamtrips.presentation;

import org.robobinding.annotation.PresentationModel;

/**
 * Created by 1 on 29.01.15.
 */
@PresentationModel
public class BookItDialogPM extends BasePresentation<BookItDialogPM.View> {

    private static final String URL_BASE = "https://www.dreamtrips.com/trips/details/%d?user=%s&token=%s&appMode=true#/book";

    private String url;

    public BookItDialogPM(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
    }

    public String getUrl() {
        return url;
    }

    public void setTripId(int tripId) {
        url = String.format(URL_BASE, tripId, appSessionHolder.get().get().getUser().getUsername(),
                appSessionHolder.get().get().getLegacyApiToken());
    }

    public interface View extends BasePresentation.View {

    }

}

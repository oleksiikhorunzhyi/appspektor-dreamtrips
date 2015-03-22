package com.worldventures.dreamtrips.modules.trips.presenter;


import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class BookItDialogPresenter extends Presenter<BookItDialogPresenter.View> {

    private static final String URL_BASE = "https://www.dreamtrips.com/trips/details/%d?user=%s&token=%s&appMode=true#/book";

    private String url;

    public BookItDialogPresenter(View view) {
        super(view);
    }

    public String getUrl() {
        return url;
    }

    public void setTripId(int tripId) {
        final String username = appSessionHolder.get().get().getUser().getUsername();
        final String legacyApiToken = appSessionHolder.get().get().getLegacyApiToken();
        url = String.format(URL_BASE, tripId, username, legacyApiToken);
    }

    public interface View extends Presenter.View {

    }
}

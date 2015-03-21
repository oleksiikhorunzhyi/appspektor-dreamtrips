package com.worldventures.dreamtrips.modules.trips.presenter;


import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;

public class BookItDialogPresenter extends BasePresenter<BookItDialogPresenter.View> {

    private static final String URL_BASE = "https://www.dreamtrips.com/trips/details/%d?user=%s&token=%s&appMode=true#/book";

    private String url;

    public BookItDialogPresenter(View view) {
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

    public interface View extends BasePresenter.View {

    }

}

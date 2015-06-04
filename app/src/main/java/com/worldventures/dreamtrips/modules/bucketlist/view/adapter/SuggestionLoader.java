package com.worldventures.dreamtrips.modules.bucketlist.view.adapter;

import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.modules.bucketlist.model.Suggestion;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

import java.util.Collections;
import java.util.List;

public class SuggestionLoader extends AutoCompleteAdapter.Loader<Suggestion> {

    protected DreamSpiceManager dreamSpiceManager;

    protected DreamTripsApi api;

    private BucketTabsPresenter.BucketType type;

    public SuggestionLoader(BucketTabsPresenter.BucketType type,
                            DreamSpiceManager dreamSpiceManager,
                            DreamTripsApi api) {
        this.type = type;
        this.dreamSpiceManager = dreamSpiceManager;
        this.api = api;
    }

    @Override
    protected List<Suggestion> request(String query) {
        if (type == BucketTabsPresenter.BucketType.LOCATIONS) {
            return api.getLocationSuggestions(query);
        } else if (type == BucketTabsPresenter.BucketType.ACTIVITIES) {
            return api.getActivitySuggestions(query);
        } else if (type == BucketTabsPresenter.BucketType.DINING) {
            return api.getDiningSuggestions(query);
        }
        return Collections.emptyList();
    }

    @Override
    public final void handleError(Exception e) {
        if (DreamSpiceManager.isLoginError(e)) {
            dreamSpiceManager.login(null);
        }
    }
}
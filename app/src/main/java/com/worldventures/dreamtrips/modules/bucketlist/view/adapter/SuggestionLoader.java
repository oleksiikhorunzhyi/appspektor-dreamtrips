package com.worldventures.dreamtrips.modules.bucketlist.view.adapter;

import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.modules.bucketlist.model.Suggestion;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;

import java.util.Collections;
import java.util.List;

public class SuggestionLoader extends AutoCompleteAdapter.Loader<Suggestion> {

    protected DreamSpiceManager dreamSpiceManager;

    protected DreamTripsApi api;

    private BucketTabsFragment.Type type;

    public SuggestionLoader(BucketTabsFragment.Type type,
                            DreamSpiceManager dreamSpiceManager,
                            DreamTripsApi api) {
        this.type = type;
        this.dreamSpiceManager = dreamSpiceManager;
        this.api = api;
    }

    @Override
    protected List<Suggestion> request(String query) {
        if (type == BucketTabsFragment.Type.LOCATIONS) {
            return api.getLocationSuggestions(query);
        } else if (type == BucketTabsFragment.Type.ACTIVITIES) {
            return api.getActivitySuggestions(query);
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
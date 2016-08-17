package com.worldventures.dreamtrips.modules.bucketlist.view.adapter;

import com.worldventures.dreamtrips.core.api.AuthRetryPolicy;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.Suggestion;

import java.util.Collections;
import java.util.List;

public class SuggestionLoader extends AutoCompleteAdapter.Loader<Suggestion> {

    protected DreamTripsApi api;
    private BucketItem.BucketType type;
    private LoginInteractor loginInteractor;

    public SuggestionLoader(BucketItem.BucketType type, DreamTripsApi api, LoginInteractor loginInteractor) {
        this.type = type;
        this.api = api;
        this.loginInteractor = loginInteractor;
    }

    @Override
    protected List<Suggestion> request(String query) {
        if (type == BucketItem.BucketType.LOCATION) {
            return api.getLocationSuggestions(query);
        } else if (type == BucketItem.BucketType.ACTIVITY) {
            return api.getActivitySuggestions(query);
        } else if (type == BucketItem.BucketType.DINING) {
            return api.getDiningSuggestions(query);
        }
        return Collections.emptyList();
    }

    @Override
    public final void handleError(Exception e) {
        if (AuthRetryPolicy.isLoginError(e)) {
            loginInteractor.loginActionPipe().send(new LoginCommand());
        }
    }
}
package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

import java.util.ArrayList;

public class GetPopularLocationQuery extends DreamTripsRequest<ArrayList<PopularBucketItem>> {

    private BucketTabsPresenter.BucketType type;
    private String query;

    public GetPopularLocationQuery(BucketTabsPresenter.BucketType type, String query) {
        super((Class<ArrayList<PopularBucketItem>>) new ArrayList<PopularBucketItem>().getClass());
        this.type = type;
        this.query = query;
    }

    @Override
    public ArrayList<PopularBucketItem> loadDataFromNetwork() throws Exception {
        ArrayList<PopularBucketItem> items = new ArrayList<>();

        if (type == BucketTabsPresenter.BucketType.LOCATIONS) {
            items.addAll(getService().getLocationPopularSuggestions(query));
        } else if (type == BucketTabsPresenter.BucketType.DINING) {
            items.addAll(getService().getDiningPopularSuggestions(query));
        } else {
            items.addAll(getService().getActivityPopularSuggestions(query));
        }

        if (!items.isEmpty()) {
            for (PopularBucketItem popularBucketItem : items) {
                popularBucketItem.setType(type.getName());
            }
        }

        return items;
    }
}

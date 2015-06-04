package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

import java.util.ArrayList;

public class GetPopularLocation extends DreamTripsRequest<ArrayList<PopularBucketItem>> {

    private BucketTabsPresenter.BucketType type;

    public GetPopularLocation(BucketTabsPresenter.BucketType type) {
        super((Class<ArrayList<PopularBucketItem>>) new ArrayList<PopularBucketItem>().getClass());
        this.type = type;
    }

    @Override
    public ArrayList<PopularBucketItem> loadDataFromNetwork() throws Exception {
        ArrayList<PopularBucketItem> list = new ArrayList<>();

        if (type == BucketTabsPresenter.BucketType.LOCATIONS) {
            list.addAll(getService().getPopularLocations());
        } else if (type == BucketTabsPresenter.BucketType.DINING) {
            list.addAll(getService().getPopularDining());
        } else {
            list.addAll(getService().getPopularActivities());
        }

        if (!list.isEmpty()) {
            for (PopularBucketItem popularBucketItem : list) {
                popularBucketItem.setType(type.getName());
            }
        }

        return list;
    }
}

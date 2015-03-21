package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;

import java.util.ArrayList;

public class GetPopularLocation extends DreamTripsRequest<ArrayList<PopularBucketItem>> {

    private BucketTabsFragment.Type type;

    public GetPopularLocation(BucketTabsFragment.Type type) {
        super((Class<ArrayList<PopularBucketItem>>) new ArrayList<PopularBucketItem>().getClass());
        this.type = type;
    }

    @Override
    public ArrayList<PopularBucketItem> loadDataFromNetwork() throws Exception {
        ArrayList<PopularBucketItem> list = new ArrayList<>();

        if (type.equals(BucketTabsFragment.Type.LOCATIONS)) {
            list.addAll(getService().getPopularLocations());
        } else {
            list.addAll(getService().getPopularActivities());
        }

        if (list.size() > 0)
            for (PopularBucketItem popularBucketItem : list) {
                popularBucketItem.setType(type.getName());
            }

        return list;
    }
}

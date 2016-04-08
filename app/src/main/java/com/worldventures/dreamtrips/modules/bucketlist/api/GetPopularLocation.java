package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

import java.util.ArrayList;

public class GetPopularLocation extends DreamTripsRequest<ArrayList<PopularBucketItem>> {

    private BucketItem.BucketType type;

    public GetPopularLocation(BucketItem.BucketType type) {
        super((Class<ArrayList<PopularBucketItem>>) new ArrayList<PopularBucketItem>().getClass());
        this.type = type;
    }

    @Override
    public ArrayList<PopularBucketItem> loadDataFromNetwork() throws Exception {
        ArrayList<PopularBucketItem> list = new ArrayList<>();

        if (type == BucketItem.BucketType.LOCATION) {
            list.addAll(getService().getPopularLocations());
        } else if (type == BucketItem.BucketType.DINING) {
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

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_popular_bl;
    }
}

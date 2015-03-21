package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
* Created by zen on 3/21/15.
*/
public class GetBucketList extends DreamTripsRequest<ArrayList<BucketItem>> {

    private BucketTabsFragment.Type type;
    private boolean fromNetwork;
    private SnappyRepository snappyRepository;
    private Prefs prefs;

    public GetBucketList(Prefs prefs, SnappyRepository snappyRepository, BucketTabsFragment.Type type, boolean fromNetwork) {
        super((Class<ArrayList<BucketItem>>) new ArrayList<BucketItem>().getClass());
        this.fromNetwork = fromNetwork;
        this.type = type;
        this.snappyRepository = snappyRepository;
        this.prefs = prefs;
    }

    @Override
    public ArrayList<BucketItem> loadDataFromNetwork() throws Exception {
        ArrayList<BucketItem> resultList = new ArrayList<>();

        if (needUpdate() || fromNetwork) {
            ArrayList<BucketItem> list = getService().getBucketList();

            ArrayList<BucketItem> activityList = new ArrayList<>();
            ArrayList<BucketItem> locationList = new ArrayList<>();

            activityList.addAll(Queryable.from(list).filter((bucketItem) -> bucketItem.getType()
                    .equalsIgnoreCase(BucketTabsFragment.Type.ACTIVITIES.getName())).toList());
            locationList.addAll(Queryable.from(list).filter((bucketItem) -> bucketItem.getType()
                    .equalsIgnoreCase(BucketTabsFragment.Type.LOCATIONS.getName())).toList());

            snappyRepository.saveBucketList(activityList, BucketTabsFragment.Type.ACTIVITIES.name());
            snappyRepository.saveBucketList(locationList, BucketTabsFragment.Type.LOCATIONS.name());

            resultList.addAll(Queryable.from(list).filter((bucketItem) -> bucketItem.getType()
                    .equalsIgnoreCase(type.getName())).toList());

            prefs.put(Prefs.LAST_SYNC_BUCKET, Calendar.getInstance().getTimeInMillis());
        } else {
            resultList.addAll(snappyRepository.readBucketList(type.name()));
        }

        return resultList;
    }

    private boolean needUpdate() throws ExecutionException, InterruptedException {
        long current = Calendar.getInstance().getTimeInMillis();
        return current - prefs.getLong(Prefs.LAST_SYNC_BUCKET) > DELTA_BUCKET || snappyRepository.isEmpty(SnappyRepository.BUCKET_LIST);
    }

}

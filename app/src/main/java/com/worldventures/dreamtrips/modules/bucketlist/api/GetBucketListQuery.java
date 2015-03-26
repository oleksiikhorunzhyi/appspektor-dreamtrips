package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class GetBucketListQuery extends Query<ArrayList<BucketItem>> {

    private BucketTabsFragment.Type type;
    private SnappyRepository snappyRepository;
    private Prefs prefs;

    public GetBucketListQuery(Prefs prefs, SnappyRepository snappyRepository, BucketTabsFragment.Type type) {
        super((Class<ArrayList<BucketItem>>) new ArrayList<BucketItem>().getClass());
        this.type = type;
        this.snappyRepository = snappyRepository;
        this.prefs = prefs;
    }

    @Override
    public ArrayList<BucketItem> loadDataFromNetwork() throws Exception {
        ArrayList<BucketItem> resultList = new ArrayList<>();

        if (needUpdate()) {
            ArrayList<BucketItem> list = getService().getBucketList(type.getName());
            snappyRepository.saveBucketList(list, type.name());
            resultList.addAll(list);

            prefs.put(Prefs.LAST_SYNC_BUCKET + type.getName(), Calendar.getInstance().getTimeInMillis());
        } else {
            resultList.addAll(snappyRepository.readBucketList(type.name()));
        }

        return resultList;
    }

    private boolean needUpdate() throws ExecutionException, InterruptedException {
        long current = Calendar.getInstance().getTimeInMillis();
        return current - prefs.getLong(Prefs.LAST_SYNC_BUCKET + type.getName()) > DELTA_BUCKET || snappyRepository.isEmpty(SnappyRepository.BUCKET_LIST);
    }

}
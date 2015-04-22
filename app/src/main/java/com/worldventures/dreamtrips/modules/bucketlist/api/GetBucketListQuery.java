package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

import java.util.ArrayList;
import java.util.Calendar;

public class GetBucketListQuery extends Query<ArrayList<BucketItem>> {

    private BucketTabsPresenter.BucketType type;
    private SnappyRepository snappyRepository;
    private Prefs prefs;

    public GetBucketListQuery(Prefs prefs, SnappyRepository snappyRepository, BucketTabsPresenter.BucketType type) {
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

    private boolean needUpdate() {
        long current = Calendar.getInstance().getTimeInMillis();
        return current - prefs.getLong(Prefs.LAST_SYNC_BUCKET + type.getName()) > DELTA_BUCKET || snappyRepository.isEmpty(SnappyRepository.BUCKET_LIST);
    }

}

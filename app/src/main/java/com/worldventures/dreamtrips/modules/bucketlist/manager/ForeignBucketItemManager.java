package com.worldventures.dreamtrips.modules.bucketlist.manager;

import android.support.annotation.NonNull;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetBucketItemsQuery;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

import java.util.List;

public class ForeignBucketItemManager extends BucketItemManager {

    public ForeignBucketItemManager(Injector injector) {
        super(injector);
    }

    @NonNull
    protected GetBucketItemsQuery getBucketListRequest() {
        return new GetBucketItemsQuery(userId);
    }

    protected List<BucketItem> readBucketItems(BucketTabsPresenter.BucketType type) {
        return snapper.readBucketList(type.name(), userId);
    }


    protected void doLocalSave(List<BucketItem> bucketItems, BucketTabsPresenter.BucketType type) {
        snapper.deleteAllForeignBucketList();
        snapper.saveBucketList(bucketItems, type.name(), userId);
    }
}

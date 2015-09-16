package com.worldventures.dreamtrips.modules.bucketlist.manager;

import android.support.annotation.NonNull;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetBucketItemsQuery;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

import java.util.List;

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType;

public class ForeignBucketItemManager extends BucketItemManager {

    public ForeignBucketItemManager(Injector injector) {
        super(injector);
    }

    @NonNull
    protected GetBucketItemsQuery getBucketListRequest() {
        return new GetBucketItemsQuery(userId);
    }

    @Override
    protected List<BucketItem> readBucketItems(BucketType type) {
        return snapper.readBucketList(type.name(), userId);
    }


    protected void doLocalSave(List<BucketItem> bucketItems, BucketType type) {
        snapper.deleteAllForeignBucketList();
        snapper.saveBucketList(bucketItems, type.name(), userId);
    }
}

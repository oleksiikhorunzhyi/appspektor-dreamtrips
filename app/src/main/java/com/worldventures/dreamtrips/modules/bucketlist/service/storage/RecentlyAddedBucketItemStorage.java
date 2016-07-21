package com.worldventures.dreamtrips.modules.bucketlist.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.RecentlyAddedBucketsFromPopularCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.worldventures.dreamtrips.modules.bucketlist.service.command.RecentlyAddedBucketsFromPopularCommand.BUCKET_TYPE_EXTRA;

public class RecentlyAddedBucketItemStorage implements ActionStorage<List<BucketItem>> {
    private Map<BucketType, List<BucketItem>> mapOfItems = new ConcurrentHashMap<>();

    @Override
    public void save(@Nullable CacheBundle params, List<BucketItem> data) {
        checkBundle(params);
        mapOfItems.put(params.get(BUCKET_TYPE_EXTRA), data);
    }

    @Override
    public List<BucketItem> get(@Nullable CacheBundle bundle) {
        checkBundle(bundle);
        BucketType type = bundle.get(BUCKET_TYPE_EXTRA);

        return !mapOfItems.containsKey(type) ? new ArrayList<>() : mapOfItems.get(type);
    }

    @Override
    public Class<? extends CachedAction> getActionClass() {
        return RecentlyAddedBucketsFromPopularCommand.class;
    }

    private void checkBundle(CacheBundle bundle) {
        if (bundle == null || !bundle.contains(BUCKET_TYPE_EXTRA)) {
            throw new AssertionError("No bucket type parameter found");
        }
    }
}

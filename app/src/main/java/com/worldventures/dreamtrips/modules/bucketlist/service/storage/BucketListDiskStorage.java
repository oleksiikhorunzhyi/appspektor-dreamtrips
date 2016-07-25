package com.worldventures.dreamtrips.modules.bucketlist.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;

import java.util.List;

public class BucketListDiskStorage implements ActionStorage<List<BucketItem>> {
    public static final String USER_ID_EXTRA = "userId";

    private final BucketMemoryStorage memoryStorage;

    private final SnappyRepository snappyRepository;

    public BucketListDiskStorage(BucketMemoryStorage memoryStorage, SnappyRepository snappyRepository) {
        this.memoryStorage = memoryStorage;
        this.snappyRepository = snappyRepository;
    }

    @Override
    public synchronized void save(@Nullable CacheBundle bundle, List<BucketItem> data) {
        if (bundle == null) {
            throw new IllegalArgumentException("User id has been provided");
        }
        int userId = bundle.get(USER_ID_EXTRA);
        memoryStorage.save(bundle, data);
        snappyRepository.saveBucketList(data, userId);
    }

    @Override
    public synchronized List<BucketItem> get(@Nullable CacheBundle bundle) {
        if (bundle == null) {
            throw new IllegalArgumentException("User id has been provided");
        }
        int userId = bundle.get(USER_ID_EXTRA);

        List<BucketItem> listOfBuckets = memoryStorage.get(bundle);
        if (listOfBuckets == null || listOfBuckets.isEmpty()) {
            listOfBuckets = snappyRepository.readBucketList(userId);
        }

        return listOfBuckets;
    }

    @Override
    public Class<BucketListCommand> getActionClass() {
        return BucketListCommand.class;
    }
}
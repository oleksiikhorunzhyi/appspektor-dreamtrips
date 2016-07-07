package com.worldventures.dreamtrips.modules.bucketlist.service.storage;

import android.support.annotation.Nullable;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;

import java.util.List;

public class BucketListDiskStorage implements ActionStorage<List<BucketItem>> {
    private final MemoryStorage<List<BucketItem>> memoryStorage;

    private final SnappyRepository snappyRepository;
    private final SessionHolder<UserSession> sessionHolder;

    public BucketListDiskStorage(MemoryStorage<List<BucketItem>> memoryStorage,
                                 SnappyRepository snappyRepository,
                                 SessionHolder<UserSession> sessionHolder) {
        this.memoryStorage = memoryStorage;

        this.snappyRepository = snappyRepository;
        this.sessionHolder = sessionHolder;
    }

    @Override
    public synchronized void save(@Nullable CacheBundle bundle, List<BucketItem> data) {
        memoryStorage.save(bundle, data);
        snappyRepository.saveBucketList(data, userId());
    }

    @Override
    public synchronized List<BucketItem> get(@Nullable CacheBundle bundle) {
        List<BucketItem> listOfBuckets = memoryStorage.get(bundle);
        if (listOfBuckets == null || listOfBuckets.isEmpty()) {
            listOfBuckets = snappyRepository.readBucketList(userId());
        }

        return listOfBuckets;
    }

    @Override
    public Class<BucketListCommand> getActionClass() {
        return BucketListCommand.class;
    }

    private int userId() {
        return sessionHolder.get().get().getUser().getId();
    }
}
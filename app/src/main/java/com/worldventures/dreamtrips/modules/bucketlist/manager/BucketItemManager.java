package com.worldventures.dreamtrips.modules.bucketlist.manager;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.api.BucketItemsLoadedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetBucketItemsQuery;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BucketItemManager {

    public BucketItemManager(Injector injector) {
        injector.inject(this);
    }

    @Inject
    SnappyRepository snapper;

    @Inject
    DreamSpiceManager dreamSpiceManager;

    @Global
    @Inject
    EventBus eventBus;

    List<BucketItem> bucketItemsLocation;
    List<BucketItem> bucketItemsActivity;
    List<BucketItem> bucketItemsDining;

    public void loadBucketItems(DreamSpiceManager.FailureListener failureListener) {
        if (!snapper.isEmpty(SnappyRepository.BUCKET_LIST)) {
            eventBus.post(new BucketItemsLoadedEvent());
        }

        dreamSpiceManager.execute(new GetBucketItemsQuery(),
                items -> {
                    saveBucketItems(items);
                    eventBus.post(new BucketItemsLoadedEvent());
                }, failureListener);
    }

    private void saveBucketItems(List<BucketItem> bucketItems) {
        for (BucketTabsPresenter.BucketType bucketType : BucketTabsPresenter.BucketType.values()) {
            List<BucketItem> filteredItems = Queryable.from(bucketItems).filter(item ->
                    item.getType().equals(bucketType.getName())).toList();
            snapper.saveBucketList(filteredItems, bucketType.name());
            switch (bucketType) {
                case LOCATIONS:
                    bucketItemsLocation = filteredItems;
                    break;
                case ACTIVITIES:
                    bucketItemsActivity = filteredItems;
                    break;
                case DINING:
                    bucketItemsDining = filteredItems;
                    break;
            }
        }
    }

    public void saveBucketItem(BucketItem item, BucketTabsPresenter.BucketType type, boolean asFirst) {
        List<BucketItem> bucketItems = getBucketItems(type);
        bucketItems.remove(item);
        if (asFirst) bucketItems.add(0, item);
        else bucketItems.add(item);
        saveBucketItems(bucketItems, type);
    }


    public void saveBucketItems(List<BucketItem> bucketItems, BucketTabsPresenter.BucketType type) {
        snapper.saveBucketList(bucketItems, type.name());
    }

    public List<BucketItem> getBucketItems(BucketTabsPresenter.BucketType type) {
        List<BucketItem> items = new ArrayList<>();
        switch (type) {
            case LOCATIONS:
                items.addAll(bucketItemsLocation);
                break;
            case ACTIVITIES:
                items.addAll(bucketItemsActivity);
                break;
            case DINING:
                items.addAll(bucketItemsDining);
                break;
        }

        if (items.isEmpty()) {
            items.addAll(snapper.readBucketList(type.name()));
        }

        return items;
    }


}

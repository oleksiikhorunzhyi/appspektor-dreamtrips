package com.worldventures.dreamtrips.modules.bucketlist.manager;

import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.AddBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.BucketItemsLoadedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.api.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.DeleteBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetBucketItemsQuery;
import com.worldventures.dreamtrips.modules.bucketlist.api.MarkBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.ReorderBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketCoverModel;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketOrderModel;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketStatusItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.trips.api.GetTripsQuery;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

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
    Prefs prefs;

    @Global
    @Inject
    EventBus eventBus;

    List<BucketItem> bucketItemsLocation;
    List<BucketItem> bucketItemsActivity;
    List<BucketItem> bucketItemsDining;

    DreamSpiceManager dreamSpiceManager;

    public void setDreamSpiceManager(DreamSpiceManager dreamSpiceManager) {
        this.dreamSpiceManager = dreamSpiceManager;
    }

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
            saveBucketItems(filteredItems, bucketType);
        }
    }

    public void saveBucketItems(List<BucketItem> bucketItems, BucketTabsPresenter.BucketType type) {
        switch (type) {
            case LOCATION:
                bucketItemsLocation = bucketItems;
                break;
            case ACTIVITY:
                bucketItemsActivity = bucketItems;
                break;
            case DINING:
                bucketItemsDining = bucketItems;
                break;
        }
        snapper.saveBucketList(bucketItems, type.name());
    }


    public void addBucketItem(BucketItem item, BucketTabsPresenter.BucketType type, boolean asFirst) {
        List<BucketItem> bucketItems = getBucketItems(type);
        bucketItems.remove(item);
        if (asFirst) bucketItems.add(0, item);
        else bucketItems.add(item);
        saveBucketItems(bucketItems, type);
    }

    public List<BucketItem> getBucketItems(BucketTabsPresenter.BucketType type) {
        List<BucketItem> items = new ArrayList<>();
        switch (type) {
            case LOCATION:
                items = bucketItemsLocation;
                break;
            case ACTIVITY:
                items = bucketItemsActivity;
                break;
            case DINING:
                items = bucketItemsDining;
                break;
        }

        if (items == null || items.isEmpty()) {
            items = snapper.readBucketList(type.name());
        }

        return items;
    }

    public BucketItem getBucketItem(BucketTabsPresenter.BucketType type, int id) {
        return Queryable.from(getBucketItems(type)).firstOrDefault(item -> item.getId() == id);
    }

    public List<BucketItem> markBucketItemAsDone(BucketItem bucketItem, BucketTabsPresenter.BucketType bucketType,
                                                 DreamSpiceManager.FailureListener failureListener) {
        //get bucket items by type
        List<BucketItem> tempItems = new ArrayList<>();
        tempItems.addAll(getBucketItems(bucketType));

        //resolve position
        int position = !bucketItem.isDone() ?
                tempItems.indexOf(Queryable.from(tempItems).firstOrDefault(BucketItem::isDone)) : 0;

        if (position < 0) position = 0;

        //remove bucket item from list
        tempItems.remove(bucketItem);

        //change status of bucket item
        bucketItem.setDone(!bucketItem.isDone());
        tempItems.add(position, bucketItem);

        //notify server about bucketItemChange
        BucketStatusItem bucketStatusItem = new BucketStatusItem(bucketItem.getStatus());
        dreamSpiceManager.execute(new MarkBucketItemCommand(bucketItem.getId(), bucketStatusItem),
                item -> saveBucketItems(tempItems, bucketType),
                failureListener::handleError);

        return tempItems;
    }

    public List<BucketItem> deleteBucketItem(BucketItem bucketItem, BucketTabsPresenter.BucketType bucketType,
                                             DreamSpiceManager.SuccessListener<JsonObject> successListener,
                                             DreamSpiceManager.FailureListener failureListener) {
        //get bucket items by type
        List<BucketItem> tempItems = new ArrayList<>();
        tempItems.addAll(getBucketItems(bucketType));
        tempItems.remove(bucketItem);
        dreamSpiceManager.execute(new DeleteBucketItemCommand(bucketItem.getId()),
                jsonObject -> {
                    if (bucketType.equals(BucketTabsPresenter.BucketType.LOCATION)) {
                        dreamSpiceManager.execute(new GetTripsQuery(snapper, prefs, false), tripModels -> {
                            TripModel tripFromBucket = Queryable.from(tripModels).firstOrDefault(element ->
                                    element.getGeoLocation().getName().equals(bucketItem.getName()));
                            if (tripFromBucket != null) {
                                tripFromBucket.setInBucketList(false);
                                snapper.saveTrip(tripFromBucket);
                            }
                        }, spiceException -> {
                        });
                    }
                    saveBucketItems(tempItems, bucketType);
                    if (successListener != null) {
                        successListener.onRequestSuccess(jsonObject);
                    }
                },
                failureListener::handleError);

        return tempItems;
    }

    public List<BucketItem> moveItem(int from, int to, BucketTabsPresenter.BucketType bucketType,
                         DreamSpiceManager.FailureListener failureListener) {
        //get bucket items by type
        List<BucketItem> tempItems = new ArrayList<>();
        tempItems.addAll(getBucketItems(bucketType));

        BucketOrderModel orderModel = new BucketOrderModel();
        orderModel.setPosition(to);

        dreamSpiceManager.execute(new ReorderBucketItemCommand(tempItems.get(from).getId(),
                orderModel), jsonObject -> {
            BucketItem item = tempItems.remove(from);
            tempItems.add(to, item);
            saveBucketItems(tempItems, bucketType);
        }, failureListener);
        return tempItems;
    }

    public void addBucketItem(String title, BucketTabsPresenter.BucketType bucketType,
                              DreamSpiceManager.SuccessListener<BucketItem> successListener,
                              DreamSpiceManager.FailureListener errorListener) {
        TrackingHelper.bucketAddStart(bucketType.name());
        BucketPostItem bucketPostItem = new BucketPostItem(bucketType.getName(), title, BucketItem.NEW);
        dreamSpiceManager.execute(new AddBucketItemCommand(bucketPostItem), bucketItem -> {
            addBucketItem(bucketItem, bucketType, true);
            TrackingHelper.bucketAddFinish(bucketType.name());
            successListener.onRequestSuccess(bucketItem);
        }, errorListener);

    }

    public void addBucketItemFromTrip(String tripId,
                                      DreamSpiceManager.SuccessListener<BucketItem> successListener,
                                      DreamSpiceManager.FailureListener errorListener) {
        dreamSpiceManager.execute(new AddBucketItemCommand(new BucketBasePostItem("trip", tripId)),
                bucketItem -> {
                    successListener.onRequestSuccess(bucketItem);
                    addBucketItem(bucketItem, BucketTabsPresenter.BucketType.LOCATION, true);
                }, errorListener);
    }

    public void addBucketItemFromPopular(PopularBucketItem popularBucketItem, boolean done,
                                         BucketTabsPresenter.BucketType bucketType,
                                         DreamSpiceManager.SuccessListener<BucketItem> successListener,
                                         DreamSpiceManager.FailureListener failureListener) {
        List<BucketItem> tempItems = new ArrayList<>();
        tempItems.addAll(getBucketItems(bucketType));
        BucketBasePostItem bucketPostItem = new BucketBasePostItem(bucketType.getName(),
                String.valueOf(popularBucketItem.getId()));
        bucketPostItem.setStatus(done);
        dreamSpiceManager.execute(new AddBucketItemCommand(bucketPostItem),
                bucketItem -> {
                    tempItems.add(0, bucketItem);
                    saveBucketItems(tempItems, bucketType);
                    int recentlyAddedBucketItems = snapper.getRecentlyAddedBucketItems(bucketType.name());
                    snapper.saveRecentlyAddedBucketItems(bucketType.name(), recentlyAddedBucketItems + 1);
                    successListener.onRequestSuccess(bucketItem);
                }, failureListener);

    }

    public void updateBucketItemCoverId(BucketItem bucketItem, int coverID,
                                        DreamSpiceManager.FailureListener failureListener) {
        updateBucketItemCoverId(bucketItem, coverID, null, failureListener);
    }


    public void updateBucketItemCoverId(BucketItem bucketItem, int coverID,
                                        @Nullable DreamSpiceManager.SuccessListener<BucketItem> successListener,
                                        DreamSpiceManager.FailureListener failureListener) {
        BucketCoverModel bucketCoverModel = new BucketCoverModel();
        bucketCoverModel.setCoverId(coverID);
        bucketCoverModel.setStatus(bucketItem.getStatus());
        bucketCoverModel.setType(bucketItem.getType());
        bucketCoverModel.setId(String.valueOf(bucketItem.getId()));
        updateBucketItem(bucketCoverModel, successListener, failureListener);
    }

    public void updateItemStatus(String id, boolean status,
                                 @Nullable DreamSpiceManager.SuccessListener<BucketItem> successListener,
                                 DreamSpiceManager.FailureListener failureListener) {
        BucketBasePostItem bucketBasePostItem = new BucketBasePostItem();
        bucketBasePostItem.setId(id);
        bucketBasePostItem.setStatus(status);
        updateBucketItem(bucketBasePostItem, successListener,
                failureListener);
    }

    public void updateBucketItem(BucketBasePostItem bucketBasePostItem,
                                 DreamSpiceManager.FailureListener failureListener) {
        updateBucketItem(bucketBasePostItem, null, failureListener);
    }

    public void updateBucketItem(BucketBasePostItem bucketBasePostItem,
                                 @Nullable DreamSpiceManager.SuccessListener<BucketItem> successListener,
                                 DreamSpiceManager.FailureListener failureListener) {
        UpdateBucketItemCommand updateBucketItemCommand =
                new UpdateBucketItemCommand(bucketBasePostItem.getId(), bucketBasePostItem);
        dreamSpiceManager.execute(updateBucketItemCommand, updatedItem -> {
            resaveBucketItem(updatedItem);
            if (successListener != null) successListener.onRequestSuccess(updatedItem);
        }, failureListener);
    }

    public void resaveBucketItem(BucketItem updatedItem) {
        BucketTabsPresenter.BucketType bucketType = getType(updatedItem.getType());
        List<BucketItem> tempItems = new ArrayList<>();
        tempItems.addAll(getBucketItems(bucketType));
        int oldPosition = tempItems.indexOf(updatedItem);
        BucketItem oldItem = tempItems.get(oldPosition);
        int newPosition = (oldItem.isDone() && !updatedItem.isDone()) ? 0 : oldPosition;
        tempItems.remove(oldPosition);
        tempItems.add(newPosition, updatedItem);
        saveBucketItems(tempItems, bucketType);
        eventBus.post(new BucketItemUpdatedEvent(updatedItem));
    }

    public void deleteBucketItemPhoto(BucketPhoto bucketPhoto, BucketItem bucketItem,
                                      DreamSpiceManager.SuccessListener<JsonObject> successListener,
                                      DreamSpiceManager.FailureListener failureListener) {
        dreamSpiceManager.execute(new DeleteBucketPhotoCommand(bucketPhoto.getFsId(),
                bucketItem.getId()), jsonObject -> {
            successListener.onRequestSuccess(jsonObject);
            bucketItem.getPhotos().remove(bucketPhoto);

            if (bucketItem.getCoverPhoto() != null &&
                    bucketItem.getCoverPhoto().equals(bucketPhoto)) {
                bucketItem.setCoverPhoto(bucketItem.getFirstPhoto());
            }

            resaveBucketItem(bucketItem);
        }, failureListener);
    }

    public BucketItem getBucketItemByPhoto(BucketPhoto bucketPhoto) {
        for (BucketTabsPresenter.BucketType type : BucketTabsPresenter.BucketType.values()) {
            BucketItem item = Queryable.from(getBucketItems(type)).firstOrDefault(bucketItem ->
                    bucketItem.getPhotos().contains(bucketPhoto));
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    private BucketTabsPresenter.BucketType getType(String name) {
        return BucketTabsPresenter.BucketType.valueOf(name.toUpperCase());
    }

}

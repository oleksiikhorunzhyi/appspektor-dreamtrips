package com.worldventures.dreamtrips.modules.bucketlist.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
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
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.NEW;

public class BucketItemManager {

    @Inject
    SnappyRepository snapper;

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

    @Inject
    Prefs prefs;

    @Global
    @Inject
    EventBus eventBus;

    List<BucketItem> bucketItemsLocation;

    List<BucketItem> bucketItemsActivity;
    List<BucketItem> bucketItemsDining;

    DreamSpiceManager dreamSpiceManager;

    public BucketItemManager(Injector injector) {
        injector.inject(this);
    }

    public void setDreamSpiceManager(DreamSpiceManager dreamSpiceManager) {
        this.dreamSpiceManager = dreamSpiceManager;
    }

    public void loadBucketItems(User user, DreamSpiceManager.FailureListener failureListener) {
        if (!snapper.isEmpty(SnappyRepository.BUCKET_LIST)) {
            eventBus.post(new BucketItemsLoadedEvent());
        }

        dreamSpiceManager.execute(getBucketListRequest(user.getId()), items -> {
            Queryable.from(items).forEachR(item -> item.setOwner(user));
            saveBucketItems(items);
            eventBus.post(new BucketItemsLoadedEvent());
        }, failureListener);
    }

    @NonNull
    protected GetBucketItemsQuery getBucketListRequest(int userId) {
        return new GetBucketItemsQuery(userId);
    }

    protected List<BucketItem> readBucketItems(BucketType type) {
        return snapper.readBucketList(type.name(), appSessionHolder.get().get().getUser().getId());
    }

    private void saveBucketItems(List<BucketItem> bucketItems) {
        for (BucketType bucketType : BucketType.values()) {
            List<BucketItem> filteredItems = Queryable.from(bucketItems).filter(item ->
                    item.getType().equals(bucketType.getName())).toList();
            saveBucketItems(filteredItems, bucketType);
        }
    }

    public void saveSingleBucketItem(BucketItem bucketItem) {
        List<BucketItem> items = Collections.singletonList(bucketItem);
        snapper.saveBucketList(items, bucketItem.getType(), bucketItem.getOwner().getId());
    }

    public BucketItem getSingleBucketItem(BucketType type, String uid, int owner) {
        return Queryable.from(snapper.readBucketList(type.getName(), owner)).firstOrDefault(item -> item.getUid().equals(uid));
    }

    public void saveBucketItems(List<BucketItem> bucketItems, BucketType type) {
        saveBucketItems(bucketItems, type, appSessionHolder.get().get().getUser().getId());
    }

    public void saveBucketItems(List<BucketItem> bucketItems, BucketType type, int userId) {
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
        snapper.saveBucketList(bucketItems, type.name(), userId);
    }

    public void addBucketItem(BucketItem item, BucketType type, boolean asFirst) {
        List<BucketItem> bucketItems = getBucketItems(type);
        bucketItems.remove(item);
        if (asFirst) bucketItems.add(0, item);
        else bucketItems.add(item);
        saveBucketItems(bucketItems, type);
    }

    public List<BucketItem> getBucketItems(BucketType type) {
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
            items = readBucketItems(type);
        }

        return items;
    }

    public BucketItem getBucketItem(BucketType type, String uid) {
        return Queryable.from(getBucketItems(type)).firstOrDefault(item -> item.getUid().equals(uid));
    }

    public List<BucketItem> markBucketItemAsDone(BucketItem bucketItem, BucketType bucketType,
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
        dreamSpiceManager.execute(new MarkBucketItemCommand(bucketItem.getUid(), bucketStatusItem),
                item -> saveBucketItems(tempItems, bucketType),
                failureListener::handleError);

        return tempItems;
    }

    public List<BucketItem> deleteBucketItem(BucketItem bucketItem, BucketType bucketType,
                                             DreamSpiceManager.SuccessListener<JsonObject> successListener,
                                             DreamSpiceManager.FailureListener failureListener) {
        //get bucket items by type
        List<BucketItem> tempItems = new ArrayList<>();
        tempItems.addAll(getBucketItems(bucketType));
        tempItems.remove(bucketItem);
        dreamSpiceManager.execute(new DeleteBucketItemCommand(bucketItem.getUid()),
                jsonObject -> {
                    saveBucketItems(tempItems, bucketType);
                    if (successListener != null) {
                        successListener.onRequestSuccess(jsonObject);
                    }
                },
                failureListener::handleError);

        return tempItems;
    }

    public List<BucketItem> moveItem(int from, int to, BucketType bucketType,
                                     DreamSpiceManager.FailureListener failureListener) {
        //get bucket items by type
        List<BucketItem> tempItems = new ArrayList<>();
        tempItems.addAll(getBucketItems(bucketType));

        BucketOrderModel orderModel = new BucketOrderModel();
        orderModel.setPosition(to);

        dreamSpiceManager.execute(new ReorderBucketItemCommand(tempItems.get(from).getUid(),
                orderModel), jsonObject -> {
            BucketItem item = tempItems.remove(from);
            tempItems.add(to, item);
            saveBucketItems(tempItems, bucketType);
        }, failureListener);
        return tempItems;
    }

    public void addBucketItem(String title, BucketType bucketType,
                              DreamSpiceManager.SuccessListener<BucketItem> successListener,
                              DreamSpiceManager.FailureListener errorListener) {
        TrackingHelper.bucketAddStart(bucketType.name());
        BucketPostItem bucketPostItem = new BucketPostItem(bucketType.getName(), title, NEW);
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
                    addBucketItem(bucketItem, BucketType.LOCATION, true);
                }, errorListener);
    }

    public void addBucketItemFromPopular(PopularBucketItem popularBucketItem, boolean done,
                                         BucketType bucketType,
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

    public void updateBucketItemCoverId(BucketItem bucketItem, String coverID,
                                        DreamSpiceManager.FailureListener failureListener) {
        updateBucketItemCoverId(bucketItem, coverID, null, failureListener);
    }


    public void updateBucketItemCoverId(BucketItem bucketItem, String coverID,
                                        @Nullable DreamSpiceManager.SuccessListener<BucketItem> successListener,
                                        DreamSpiceManager.FailureListener failureListener) {
        BucketCoverModel bucketCoverModel = new BucketCoverModel();
        bucketCoverModel.setCoverId(coverID);
        bucketCoverModel.setStatus(bucketItem.getStatus());
        bucketCoverModel.setType(bucketItem.getType());
        bucketCoverModel.setId(bucketItem.getUid());
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
        BucketType bucketType = getType(updatedItem.getType());
        List<BucketItem> tempItems = new ArrayList<>();
        tempItems.addAll(getBucketItems(bucketType));
        int oldPosition = tempItems.indexOf(updatedItem);
        BucketItem oldItem = tempItems.get(oldPosition);
        int newPosition = (oldItem.isDone() && !updatedItem.isDone()) ? 0 : oldPosition;
        tempItems.remove(oldPosition);
        tempItems.add(newPosition, updatedItem);
        if (updatedItem.getOwner() == null) {
            updatedItem.setOwner(oldItem.getOwner());
        }
        saveBucketItems(tempItems, bucketType);

        eventBus.post(new BucketItemUpdatedEvent(updatedItem));
    }

    public void deleteBucketItemPhoto(BucketPhoto bucketPhoto, BucketItem bucketItem,
                                      DreamSpiceManager.SuccessListener<JsonObject> successListener,
                                      DreamSpiceManager.FailureListener failureListener) {
        dreamSpiceManager.execute(new DeleteBucketPhotoCommand(bucketPhoto.getFSId(),
                bucketItem.getUid()), jsonObject -> {
            successListener.onRequestSuccess(jsonObject);
            bucketItem.getPhotos().remove(bucketPhoto);

            if (bucketItem.getCoverPhoto() != null &&
                    bucketItem.getCoverPhoto().equals(bucketPhoto)) {
                bucketItem.setCoverPhoto(bucketItem.getFirstPhoto());
            }

            resaveBucketItem(bucketItem);
        }, failureListener);
    }

    public void updateBucketItemWithPhoto(BucketItem bucketItem, BucketPhoto photo) {
        if (bucketItem.getCoverPhoto() == null) {
            bucketItem.setCoverPhoto(photo);
        }
        bucketItem.getPhotos().add(0, photo);
        resaveBucketItem(bucketItem);
    }

    public BucketItem getBucketItemByPhoto(BucketPhoto bucketPhoto) {
        for (BucketType type : BucketType.values()) {
            BucketItem item = Queryable.from(getBucketItems(type)).firstOrDefault(bucketItem ->
                    bucketItem.getPhotos().contains(bucketPhoto));
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    public BucketType getType(String name) {
        return BucketType.valueOf(name.toUpperCase());
    }
}

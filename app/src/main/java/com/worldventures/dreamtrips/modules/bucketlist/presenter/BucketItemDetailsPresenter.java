package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemDeleteConfirmedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemShared;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.DiningItem;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;

import java.util.List;

public class BucketItemDetailsPresenter extends BucketDetailsBasePresenter<BucketItemDetailsPresenter.View> {

    public BucketItemDetailsPresenter(BucketBundle bundle) {
        super(bundle);
    }

    public void onEdit() {
        BucketBundle bundle = new BucketBundle();
        bundle.setType(type);
        bundle.setBucketItemUid(bucketItemId);

        fragmentCompass.removeEdit();
        if (view.isTabletLandscape()) {
            fragmentCompass.disableBackStack();
            fragmentCompass.setContainerId(R.id.container_details_floating);
            fragmentCompass.showContainer();
            NavigationBuilder.create().with(fragmentCompass).data(bundle).attach(Route.BUCKET_EDIT);
        } else {
            bundle.setLock(true);
            NavigationBuilder.create().with(activityRouter).data(bundle).move(Route.BUCKET_EDIT);
        }
        eventBus.post(new BucketItemAnalyticEvent(bucketItem.getUid(), TrackingHelper.ATTRIBUTE_EDIT));
    }

    public void onDelete() {
        eventBus.post(new BucketItemAnalyticEvent(bucketItem.getUid(), TrackingHelper.ATTRIBUTE_DELETE));
        view.showDeletionDialog(bucketItem);
    }

    public void deleteBucketItem(BucketItem bucketItem) {
        view.showProgressDialog();
        getBucketItemManager().deleteBucketItem(bucketItem, type,
                jsonObject -> {
                    view.dismissProgressDialog();
                    if (!view.isTabletLandscape()) view.done();
                    eventBus.post(new BucketItemUpdatedEvent(bucketItem));
                },
                spiceException -> {
                    BucketItemDetailsPresenter.super.handleError(spiceException);
                    view.dismissProgressDialog();
                });
    }

    public void onStatusUpdated(boolean status) {
        if (bucketItem != null && status != bucketItem.isDone()) {
            view.disableMarkAsDone();
            getBucketItemManager().updateItemStatus(String.valueOf(bucketItemId),
                    status, item -> view.enableMarkAsDone(), spiceException -> {
                        BucketItemDetailsPresenter.super.handleError(spiceException);
                        view.setStatus(bucketItem.isDone());
                        view.enableMarkAsDone();
                    });

            eventBus.post(new BucketItemAnalyticEvent(bucketItem.getUid(), TrackingHelper.ATTRIBUTE_MARK_AS_DONE));
        }
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        TrackingHelper.bucketItemView(type.getName(), bucketItemId);
    }

    @Override
    protected void syncUI(List<UploadTask> tasks) {
        super.syncUI(tasks);
        if (bucketItem != null) {
            if (!TextUtils.isEmpty(bucketItem.getType())) {
                String s = bucketItem.getCategoryName();
                view.setCategory(s);
            }
            view.setPlace(BucketItemInfoUtil.getPlace(bucketItem));
            view.setupDiningView(bucketItem.getDining());
        }
    }

    public void onEvent(MarkBucketItemDoneEvent event) {
        if (event.getBucketItem().getUid().equals(bucketItemId)) {
            updateBucketItem(event.getBucketItem());
            syncUI();
        }
    }

    public void onEvent(BucketItemDeleteConfirmedEvent event) {
        if (bucketItemId.equals(event.getBucketItemId())) {
            deleteBucketItem(bucketItem);
        }
    }

    public void onEvent(FeedEntityChangedEvent event) {
        if (event.getFeedEntity().getUid().equals(bucketItemId)) {
            updateBucketItem((BucketItem) event.getFeedEntity());
            syncUI();
        }
    }

    public void onEvent(BucketItemShared event) {
        eventBus.post(new BucketItemAnalyticEvent(bucketItem.getUid(), TrackingHelper.ATTRIBUTE_SHARE));
    }

    private void updateBucketItem(BucketItem updatedItem) {
        BucketItem tempItem = bucketItem;
        bucketItem = updatedItem;
        if (bucketItem.getUser() == null) {
            bucketItem.setUser(tempItem.getUser());
        }
        bucketItemManager.saveSingleBucketItem(bucketItem);
    }

    public interface View extends BucketDetailsBasePresenter.View {
        void setCategory(String category);

        void setPlace(String place);

        void disableMarkAsDone();

        void enableMarkAsDone();

        void setupDiningView(DiningItem diningItem);

        void showDeletionDialog(BucketItem bucketItem);

        void showProgressDialog();

        void dismissProgressDialog();
    }
}

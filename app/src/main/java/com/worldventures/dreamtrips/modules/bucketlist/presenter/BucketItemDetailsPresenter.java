package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.DiningItem;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;

public class BucketItemDetailsPresenter extends BucketDetailsBasePresenter<BucketItemDetailsPresenter.View> {

    public BucketItemDetailsPresenter(Bundle bundle) {
        super(bundle);
    }

    public void onEdit() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BucketActivity.EXTRA_TYPE, type);
        bundle.putInt(BucketActivity.EXTRA_ITEM, bucketItemId);
        fragmentCompass.removeEdit();
        if (view.isTabletLandscape()) {
            view.showEditContainer();
            fragmentCompass.disableBackStack();
            fragmentCompass.setContainerId(R.id.container_details_floating);
            fragmentCompass.add(Route.BUCKET_EDIT, bundle);
        } else {
            activityRouter.openComponentActivity(Route.BUCKET_EDIT, bundle);
        }
    }

    public void onDelete() {
        view.showDeletionDialog(bucketItem);
    }

    public void deleteBucketItem(BucketItem bucketItem) {
        bucketItemManager.deleteBucketItem(bucketItem, type,
                jsonObject -> {
                    if (!view.isTabletLandscape()) view.done();
                    else eventBus.post(new BucketItemUpdatedEvent(bucketItem));
                },
                this);
    }

    public void onStatusUpdated(boolean status) {
        if (bucketItem != null && status != bucketItem.isDone()) {
            view.disableCheckbox();
            bucketItemManager.updateItemStatus(String.valueOf(bucketItemId),
                    status, item -> view.enableCheckbox(), this);
        }
    }

    public void onFbShare() {
        activityRouter.openShareFacebook(bucketItem.getUrl(), null,
                String.format(context.getString(R.string.bucketlist_share),
                        bucketItem.getName()));
    }

    public void onTwitterShare() {
        activityRouter.openShareTwitter(null, bucketItem.getUrl(),
                String.format(context.getString(R.string.bucketlist_share),
                        bucketItem.getName()));
    }

    public void onEvent(MarkBucketItemDoneEvent event) {
        if (event.getBucketItem().equals(bucketItem)) {
            bucketItem = event.getBucketItem();
            syncUI();
        }
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        TrackingHelper.bucketItemView(type.getName(), bucketItemId);
    }

    @Override
    protected void syncUI() {
        super.syncUI();
        if (!TextUtils.isEmpty(bucketItem.getType())) {
            String s = bucketItem.getCategoryName();
            view.setCategory(s);
        }
        view.setPlace(BucketItemInfoUtil.getPlace(bucketItem));
        String medium = BucketItemInfoUtil.getMediumResUrl(context, bucketItem);
        String original = BucketItemInfoUtil.getHighResUrl(context, bucketItem);
        view.setCover(medium, original);
        view.setupDiningView(bucketItem.getDining());
    }

    public interface View extends BucketDetailsBasePresenter.View {
        void setCategory(String category);

        void setPlace(String place);

        void setCover(String medium, String original);

        void disableCheckbox();

        void enableCheckbox();

        void setupDiningView(DiningItem diningItem);

        void showDeletionDialog(BucketItem bucketItem);

        void showEditContainer();
    }

}

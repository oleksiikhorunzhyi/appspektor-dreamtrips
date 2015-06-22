package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.DiningItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;

public class BucketItemDetailsPresenter extends BucketDetailsBasePresenter<BucketItemDetailsPresenter.View> {

    public BucketItemDetailsPresenter(Bundle bundle) {
        super(bundle);
    }

    public void onEdit() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BucketActivity.EXTRA_TYPE, type);
        bundle.putSerializable(BucketActivity.EXTRA_ITEM, bucketItem);
        fragmentCompass.removeEdit();
        if (view.isTabletLandscape()) {
            view.showEditContainer();
            fragmentCompass.disableBackStack();
            fragmentCompass.setContainerId(R.id.container_details_floating);
            fragmentCompass.add(Route.BUCKET_EDIT, bundle);
        } else {
            activityRouter.openBucketItemEditActivity(bundle);
        }
    }

    public void onStatusUpdated(boolean status) {
        if (status != bucketItem.isDone()) {
            view.disableCheckbox();
            BucketBasePostItem bucketBasePostItem = new BucketBasePostItem();
            bucketBasePostItem.setStatus(status);
            saveBucketItem(bucketBasePostItem);
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
        TrackingHelper.bucketItemView(bucketItem.getType(), bucketItem.getId());
    }

    @Override
    protected void syncUI() {
        super.syncUI();
        if (!TextUtils.isEmpty(bucketItem.getType())) {
            String s = Character.toUpperCase(bucketItem.getType().charAt(0)) + bucketItem.getType().substring(1);
            view.setCategory(s);
        }
        view.setPlace(getPlace());
        view.setCover();
        view.updatePhotos();
        view.setupDiningView(bucketItem.getDining());
    }

    public String getMediumResUrl() {
        int width = context.getResources().getDimensionPixelSize(R.dimen.bucket_popular_photo_width);
        return bucketItem.getCoverUrl(width, width);
    }

    public String getHighResUrl() {
        int width = context.getResources().getDimensionPixelSize(R.dimen.bucket_popular_cover_width);
        return bucketItem.getCoverUrl(width, width);
    }

    @Override
    protected void onSuccess(BucketItem bucketItemUpdated) {
        super.onSuccess(bucketItemUpdated);
        view.enableCheckbox();
    }

    private String getPlace() {
        String place = null;
        if (bucketItem.getLocation() != null) {
            place = bucketItem.getLocation().getName();
        }
        if (bucketItem.getDining() != null && !TextUtils.isEmpty(bucketItem.getDining().getCity())
                && !TextUtils.isEmpty(bucketItem.getDining().getCountry())) {
            place = TextUtils.join(", ", new String[]{bucketItem.getDining().getCity(),
                    bucketItem.getDining().getCountry()});
        }
        return place;
    }

    public interface View extends BucketDetailsBasePresenter.View {
        void setCategory(String category);

        void setPlace(String place);

        void setCover();

        void showEditContainer();

        void updatePhotos();

        void disableCheckbox();

        void enableCheckbox();

        void setupDiningView(DiningItem diningItem);
    }

}

package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetCategoryQuery;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemPhotoAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType;

public class BucketTabsPresenter extends Presenter<BucketTabsPresenter.View> {

    @Inject
    SnappyRepository db;

    @Inject
    BucketItemManager bucketItemManager;

    BucketType currentType;

    @Override
    public void onInjected() {
        super.onInjected();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        setTabs();
        loadCategories();
        getBucketItemManager().setDreamSpiceManager(dreamSpiceManager);
        getBucketItemManager().loadBucketItems(getUser(), this);
    }

    protected User getUser() {
        return getAccount();
    }

    @Override
    public void onResume() {
        getBucketItemManager().setDreamSpiceManager(dreamSpiceManager);
        setRecentBucketItemsCounts();
    }

    private void loadCategories() {
        doRequest(new GetCategoryQuery(),
                categoryItems -> db.putList(SnappyRepository.CATEGORIES, categoryItems));
    }

    public void setTabs() {
        view.setTypes(Arrays.asList(BucketType.LOCATION, BucketType.ACTIVITY, BucketType.DINING));
        view.updateSelection();
    }

    public void onTabChange(BucketType type) {
        currentType = type;
        db.saveRecentlyAddedBucketItems(type.name(), 0);
        view.resetRecentlyAddedBucketItem(type);
    }

    private void setRecentBucketItemsCounts() {
        Map<BucketItem.BucketType, Integer> recentBucketItems = new HashMap<>();
        for (BucketType type : BucketType.values()) {
            recentBucketItems.put(type, db.getRecentlyAddedBucketItems(type.name()));
        }
        view.setRecentBucketItemsCount(recentBucketItems);
    }

    public void onEvent(BucketAnalyticEvent event) {
        TrackingHelper.actionBucket(event.getActionAttribute(), getTabAttributeAnalytic());
    }

    public void onEvent(BucketItemAnalyticEvent event) {
        TrackingHelper.actionBucketItem(event.getActionAttribute(), event.getBucketItemId());
    }

    public void onEvent(BucketItemPhotoAnalyticEvent event) {
        TrackingHelper.actionBucketItemPhoto(event.getActionAttribute(), event.getBucketItemId());
    }

    private String getTabAttributeAnalytic() {
        String tabAttribute = "";
        switch (view.getCurrentTabPosition()) {
            case 0:
                tabAttribute = TrackingHelper.ATTRIBUTE_LOCATIONS;
                break;
            case 1:
                tabAttribute = TrackingHelper.ATTRIBUTE_ACTIVITIES;
                break;
            case 2:
                tabAttribute = TrackingHelper.ATTRIBUTE_DINING;
                break;
        }
        return tabAttribute;
    }

    public interface View extends Presenter.View {
        void setTypes(List<BucketType> type);

        void setRecentBucketItemsCount(Map<BucketType, Integer> items);

        void resetRecentlyAddedBucketItem(BucketType type);

        void updateSelection();

        int getCurrentTabPosition();
    }

    protected <T extends BucketItemManager> T getBucketItemManager() {
        return (T) bucketItemManager;
    }
}

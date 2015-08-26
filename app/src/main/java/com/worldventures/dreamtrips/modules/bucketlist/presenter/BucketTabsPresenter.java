package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;
import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetCategoryQuery;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter.BucketType.ACTIVITY;
import static com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter.BucketType.DINING;
import static com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter.BucketType.LOCATION;

public class BucketTabsPresenter extends Presenter<BucketTabsPresenter.View> {

    @Inject
    SnappyRepository db;

    /**
     * Use getter getBucketItemManager() because we have
     * {@link com.worldventures.dreamtrips.modules.bucketlist.manager.ForeignBucketItemManager}
     */
    @Inject
    BucketItemManager bucketItemManager;

    BucketType currentType;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        setTabs();
        loadCategories();
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        super.saveInstanceState(outState);
    }

    @Override
    public void onResume() {
        getBucketItemManager().setDreamSpiceManager(dreamSpiceManager);
        getBucketItemManager().loadBucketItems(this);
        setRecentBucketItemsCounts();
    }

    private void loadCategories() {
        doRequest(new GetCategoryQuery(),
                categoryItems -> db.putList(SnappyRepository.CATEGORIES, categoryItems));
    }

    public void setTabs() {
        view.setTypes(Arrays.asList(LOCATION, ACTIVITY, DINING));
        view.updateSelection();
    }

    public void onTabChange(BucketType type) {
        currentType = type;
        db.saveRecentlyAddedBucketItems(type.name(), 0);
        view.resetRecentlyAddedBucketItem(type);
    }

    private void setRecentBucketItemsCounts() {
        Map<BucketType, Integer> recentBucketItems = new HashMap<>();
        for (BucketType type : BucketType.values()) {
            recentBucketItems.put(type, db.getRecentlyAddedBucketItems(type.name()));
        }
        view.setRecentBucketItemsCount(recentBucketItems);
    }


    public interface View extends Presenter.View {
        void setTypes(List<BucketType> type);

        void setRecentBucketItemsCount(Map<BucketType, Integer> items);

        void resetRecentlyAddedBucketItem(BucketType type);

        void updateSelection();
    }


    protected <T extends BucketItemManager> T getBucketItemManager() {
        return (T) bucketItemManager;
    }

    public enum BucketType {
        LOCATION("location", R.string.bucket_locations),
        ACTIVITY("activity", R.string.bucket_activities),
        DINING("dining", R.string.bucket_restaurants);

        protected String name;
        protected int res;

        BucketType(String name, @StringRes int res) {
            this.name = name;
            this.res = res;
        }

        public String getName() {
            return name;
        }

        @StringRes
        public int getRes() {
            return res;
        }

    }

}

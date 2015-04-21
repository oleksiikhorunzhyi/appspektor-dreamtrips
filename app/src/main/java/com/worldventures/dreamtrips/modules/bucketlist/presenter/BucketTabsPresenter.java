package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.support.annotation.StringRes;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetCategoryQuery;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter.BucketType.*;
import static com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter.BucketType.ACTIVITIES;

public class BucketTabsPresenter extends Presenter<BucketTabsPresenter.View> {

    @Inject SnappyRepository repo;

    public BucketTabsPresenter(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        loadCategories();
    }

    @Override
    public void resume() {
        setTabs();
        setRecentBucketItemsCounts();
    }

    private void loadCategories() {
        dreamSpiceManager.execute(new GetCategoryQuery(), categoriesRequestListener);
    }

    private RequestListener<ArrayList<CategoryItem>> categoriesRequestListener = new RequestListener<ArrayList<CategoryItem>>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
        }

        @Override
        public void onRequestSuccess(ArrayList<CategoryItem> categoryItems) {
            repo.putList(SnappyRepository.CATEGORIES, categoryItems);
        }
    };

    public void setTabs() {
        view.setTypes(Arrays.asList(LOCATIONS, ACTIVITIES));
    }

    private void setRecentBucketItemsCounts() {
        Map<BucketType, Integer> recentBucketItems = new HashMap<>();
        for (BucketType type : BucketType.values()) {
            recentBucketItems.put(type, repo.getRecentlyAddedBucketItems(type.name));
        }
        view.setRecentBucketItemsCount(recentBucketItems);
    }

    public interface View extends Presenter.View {
        void setTypes(List<BucketType> type);
        void setRecentBucketItemsCount(Map<BucketType, Integer> items);
    }

    public enum BucketType {
        LOCATIONS("location", R.string.bucket_locations),
        ACTIVITIES("activity", R.string.bucket_activities),
        RESTAURANTS("dinning", R.string.dinning);

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

package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetCategoryQuery;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.ArrayList;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BucketTabsPresenter extends Presenter<BucketTabsPresenter.View> {

    @Inject
    protected SnappyRepository snappyRepository;

    private RequestListener<ArrayList<CategoryItem>> categoriesRequestListener = new RequestListener<ArrayList<CategoryItem>>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //do nothing
        }

        @Override
        public void onRequestSuccess(ArrayList<CategoryItem> categoryItems) {
            snappyRepository.putList(categoryItems, SnappyRepository.CATEGORIES);
        }
    };

    public BucketTabsPresenter(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        loadCategories();
    }

    public Bundle getBundleForPosition(int position) {
        Bundle args = new Bundle();
        BucketTabsFragment.Type type = BucketTabsFragment.Type.values()[position];
        args.putSerializable(BucketListFragment.BUNDLE_TYPE, type);
        return args;
    }

    private void loadCategories() {
        dreamSpiceManager.execute(new GetCategoryQuery(), categoriesRequestListener);
    }

    public interface View extends Presenter.View {
    }

}

package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.app.Activity;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetPopularLocation;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetPopularLocationQuery;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;

import java.util.List;

import javax.inject.Inject;

public class BucketPopularPresenter extends Presenter<BucketPopularPresenter.View> {

    @Inject
    Activity activity;
    @Inject
    BucketItemManager bucketItemManager;

    private BucketItem.BucketType type;

    SweetDialogHelper sweetDialogHelper;

    public BucketPopularPresenter(BucketItem.BucketType type) {
        super();
        this.type = type;
        sweetDialogHelper = new SweetDialogHelper();
    }

    @Override
    public void onResume() {
        super.onResume();
        bucketItemManager.setDreamSpiceManager(dreamSpiceManager);

        if (view.getAdapter().getCount() == 0) reload();
    }

    public void onSearch(String constraint) {
        if (constraint.length() > 2) {
            view.startLoading();
            doRequest(new GetPopularLocationQuery(type, constraint), this::onSearchSucceed);
        }
    }

    public void onSearchSucceed(List<PopularBucketItem> items) {
        if (view != null) {
            view.finishLoading();
            view.getAdapter().setFilteredItems(items);
        }
    }

    public void searchClosed() {
        view.getAdapter().flushFilter();
    }

    public void onAdd(PopularBucketItem popularBucketItem, int position){
        add(popularBucketItem, false, position);
    }

    public void onDone(PopularBucketItem popularBucketItem, int position){
        add(popularBucketItem, true, position);
    }

    private void add(PopularBucketItem popularBucketItem, boolean done, int position) {
        bucketItemManager.addBucketItemFromPopular(popularBucketItem, done, type,
                item -> {
                    if (view != null) {
                        eventBus.post(new BucketItemUpdatedEvent(item));
                        sweetDialogHelper.notifyItemAddedToBucket(activity, item);
                        view.getAdapter().remove(popularBucketItem);
                    }
                },
                spiceException -> {
                    if (view != null) {
                        popularBucketItem.setLoading(false);
                        view.getAdapter().notifyDataSetChanged();
                        handleError(spiceException);
                    }
                });
    }

    public void reload() {
        view.startLoading();
        doRequest(new GetPopularLocation(type), items -> {
            view.finishLoading();
            //
            view.getAdapter().clear();
            view.getAdapter().addItems(items);
            view.getAdapter().notifyDataSetChanged();
        });
    }

    @Override
    public void handleError(SpiceException error) {
        view.finishLoading();
        super.handleError(error);
    }

    public interface View extends Presenter.View {

        FilterableArrayListAdapter<PopularBucketItem> getAdapter();

        void startLoading();

        void finishLoading();
    }

}
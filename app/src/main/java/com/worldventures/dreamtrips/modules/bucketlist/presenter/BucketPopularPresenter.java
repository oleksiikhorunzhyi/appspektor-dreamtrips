package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.app.Activity;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.events.AddPressedEvent;
import com.worldventures.dreamtrips.core.utils.events.DonePressedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetPopularLocation;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetPopularLocationQuery;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BucketPopularPresenter extends Presenter<BucketPopularPresenter.View> {

    @Inject
    Activity activity;
    @Inject
    BucketItemManager bucketItemManager;

    private BucketTabsPresenter.BucketType type;

    SweetDialogHelper sweetDialogHelper;

    protected DreamSpiceAdapterController<PopularBucketItem> adapterController = new DreamSpiceAdapterController<PopularBucketItem>() {
        @Override
        public SpiceRequest<ArrayList<PopularBucketItem>> getReloadRequest() {
            return new GetPopularLocation(type);
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<PopularBucketItem> items, SpiceException spiceException) {
            if (adapterController != null) {
                view.finishLoading();
                if (spiceException != null) {
                    handleError(spiceException);
                }
            }
        }
    };

    public BucketPopularPresenter(BucketTabsPresenter.BucketType type) {
        super();
        this.type = type;
        sweetDialogHelper = new SweetDialogHelper();
    }

    @Override
    public void onResume() {
        super.onResume();
        bucketItemManager.setDreamSpiceManager(dreamSpiceManager);

        if (view.getAdapter().getCount() == 0) {
            adapterController.setSpiceManager(dreamSpiceManager);
            adapterController.setAdapter(view.getAdapter());
            adapterController.reload();
        }
    }

    @Override
    public void dropView() {
        adapterController.setAdapter(null);
        super.dropView();
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

    public void onEvent(AddPressedEvent event) {
        if (event.getPopularBucketItem().getType().equalsIgnoreCase(type.getName())) {
            add(event.getPopularBucketItem(), false, event.getPosition());
            eventBus.cancelEventDelivery(event);
        }
    }

    public void onEvent(DonePressedEvent event) {
        if (event.getPopularBucketItem().getType().equalsIgnoreCase(type.getName())) {
            add(event.getPopularBucketItem(), true, event.getPosition());
            eventBus.cancelEventDelivery(event);
        }
    }

    private void add(PopularBucketItem popularBucketItem, boolean done, int position) {
        bucketItemManager.addBucketItemFromPopular(popularBucketItem, done, type,
                item -> {
                    sweetDialogHelper.notifyItemAddedToBucket(activity, item);
                    view.getAdapter().remove(popularBucketItem);
                    view.getAdapter().notifyItemRemoved(position);
                },
                spiceException -> {
                    popularBucketItem.setLoading(false);
                    view.getAdapter().notifyDataSetChanged();
                    handleError(spiceException);
                });
    }

    public void reload() {
        adapterController.reload();
    }

    public interface View extends Presenter.View {
        FilterableArrayListAdapter<PopularBucketItem> getAdapter();

        void startLoading();

        void finishLoading();
    }

}
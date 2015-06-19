package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.app.Activity;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.events.AddPressedEvent;
import com.worldventures.dreamtrips.core.utils.events.DonePressedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.api.AddBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetPopularLocation;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetPopularLocationQuery;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemAddedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
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
    protected SnappyRepository db;

    private BucketTabsPresenter.BucketType type;
    private List<BucketItem> realData = new ArrayList<>();

    BucketHelper bucketHelper;

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
        bucketHelper = new BucketHelper();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        realData.addAll(db.readBucketList(type.name()));
    }

    @Override
    public void onResume() {
        super.onResume();
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
        BucketBasePostItem bucketPostItem = new BucketBasePostItem(type.getName(),
                String.valueOf(popularBucketItem.getId()));
        bucketPostItem.setStatus(done);
        doRequest(new AddBucketItemCommand(bucketPostItem),
                (bucketItem) -> {
                    view.getAdapter().remove(popularBucketItem);
                    view.getAdapter().notifyItemRemoved(position);
                    eventBus.post(new BucketItemAddedEvent(bucketItem));
                    realData.add(0, bucketItem);
                    db.saveBucketList(realData, type.name());
                    int recentlyAddedBucketItems = db.getRecentlyAddedBucketItems(type.name);
                    db.saveRecentlyAddedBucketItems(type.name, recentlyAddedBucketItems + 1);
                    bucketHelper.notifyItemAddedToBucket(activity, bucketItem);
                },
                (spiceException) -> {
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
package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.gson.JsonObject;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.DeleteBucketItemEvent;
import com.worldventures.dreamtrips.core.utils.events.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.AddBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetBucketListQuery;
import com.worldventures.dreamtrips.modules.bucketlist.api.MarkBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.ReorderBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemAddedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemClickedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketHeader;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketOrderModel;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketStatusItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.AutoCompleteAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.SuggestionLoader;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

public class BucketListPresenter extends Presenter<BucketListPresenter.View> {

    private static final int DELETION_DELAY = 3500;

    @Inject
    protected SnappyRepository db;

    @Inject
    protected DreamTripsApi api;

    @Inject
    protected Prefs prefs;

    private BucketTabsFragment.Type type;
    private boolean showToDO = true;
    private boolean showCompleted = true;
    private List<BucketItem> bucketItems = new ArrayList<>();

    public BucketListPresenter(View view, BucketTabsFragment.Type type) {
        super(view);
        this.type = type;
    }

    @Override
    public void init() {
        super.init();
        TrackingHelper.bucketList(getUserId());
    }

    public boolean isConnected() {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        return i != null && i.isConnected() && i.isAvailable();
    }

    public void loadBucketItems() {
        if (isConnected()) {
            view.startLoading();
            dreamSpiceManager.execute(new GetBucketListQuery(prefs, db, type),
                    new RequestListener<ArrayList<BucketItem>>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            view.informUser(context.getString(R.string.could_not_load) + type.getName());
                        }

                        @Override
                        public void onRequestSuccess(ArrayList<BucketItem> result) {
                            view.finishLoading();
                            addItems(result);
                        }
                    });
        } else {
            addItems(db.readBucketList(type.name()));
        }
    }

    private void addItems(Collection<? extends BucketItem> result) {
        bucketItems.clear();
        bucketItems.addAll(result);
        refresh();
    }

    private void refresh() {
        ArrayList<Object> result = new ArrayList<>();
        if (bucketItems.size() > 0) {
            Collection<BucketItem> toDo = Queryable.from(bucketItems).
                    filter((bucketItem) -> !bucketItem.isDone()).toList();
            Collection<BucketItem> done = Queryable.from(bucketItems).
                    filter((bucketItem) -> bucketItem.isDone()).toList();
            if (showToDO && toDo.size() > 0) {
                result.addAll(toDo);
            }
            if (showCompleted && done.size() > 0) {
                result.add(new BucketHeader(0, R.string.completed));
                result.addAll(done);
            }
        }
        view.getAdapter().setItems(result);
    }

    public void onEvent(BucketItemClickedEvent event) {
        if (bucketItems.contains(event.getBucketItem())) {
            eventBus.cancelEventDelivery(event);
            openDetails(event.getBucketItem(), Route.DETAIL_BUCKET);
        }
    }

    public void onEvent(BucketItemUpdatedEvent event) {
        addItems(db.readBucketList(type.name()));
    }

    public void onEvent(BucketItemAddedEvent event) {
        if (!bucketItems.contains(event.getBucketItem())
                && event.getBucketItem().getType().equalsIgnoreCase(type.getName())) {
            if (event.getBucketItem().isDone()) {
                bucketItems.add(0, event.getBucketItem());
                refresh();
            } else {
                bucketItems.add(0, event.getBucketItem());
                refresh();
            }
            eventBus.cancelEventDelivery(event);
        }
    }

    public void onEvent(MarkBucketItemDoneEvent event) {
        boolean isNamesEquals = event.getBucketItem().getType().equalsIgnoreCase(type.getName());
        if (bucketItems.size() > 0 && isNamesEquals) {
            eventBus.cancelEventDelivery(event);
            BucketItem bucketItem = event.getBucketItem();

            moveItem(bucketItem, bucketItem.isDone()
                    ? bucketItems.indexOf(Queryable.from(bucketItems).first(BucketItem::isDone))
                    : 0);

            BucketStatusItem bucketStatusItem = new BucketStatusItem(bucketItem.getStatus());

            dreamSpiceManager.execute(new MarkBucketItemCommand(event.getBucketItem().getId(),
                            bucketStatusItem),
                    new RequestListener<BucketItem>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            view.informUser(spiceException.getMessage());
                        }

                        @Override
                        public void onRequestSuccess(BucketItem jsonObject) {
                            //nothing to do here
                        }
                    });

            bucketItems.get(bucketItems.indexOf(bucketItem)).setDone(bucketItem.isDone());
            db.saveBucketList(bucketItems, type.name());
            refresh();
        }
    }

    public void onEvent(DeleteBucketItemEvent event) {
        boolean isNamesEquals = event.getBucketItem().getType().equalsIgnoreCase(type.getName());
        if (bucketItems.size() > 0 && isNamesEquals) {
            eventBus.cancelEventDelivery(event);

            int index = bucketItems.indexOf(event.getBucketItem());
            bucketItems.remove(event.getBucketItem());
            refresh();

            DeleteBucketItemCommand request = deleteDellayed(event.getBucketItem().getId());
            view.showUndoBar((v) -> undo(event.getBucketItem(), index, request));
        }
    }

    private void openDetails(BucketItem bucketItem, Route route) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BucketActivity.EXTRA_TYPE, type);
        bundle.putSerializable(BucketActivity.EXTRA_ITEM, bucketItem);
        if (view.isTabletLandscape()) {
            view.showDetailsContainer();
            fragmentCompass.disableBackStack();
            fragmentCompass.setContainerId(R.id.container_bucket_details);
            fragmentCompass.add(Route.DETAIL_BUCKET, bundle);
        } else {
            activityRouter.openBucketItemDetails(bundle);
        }
    }

    public void addPopular() {
        activityRouter.openBucketListPopularActivity(type);
    }

    private DeleteBucketItemCommand deleteDellayed(int id) {
        DeleteBucketItemCommand request = new DeleteBucketItemCommand(id, DELETION_DELAY);
        dreamSpiceManager.execute(request, new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                view.informUser(spiceException.getMessage());
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                db.saveBucketList(bucketItems, type.name());
            }
        });
        return request;
    }

    private void undo(BucketItem bucketItem, int index, DeleteBucketItemCommand request) {
        request.setCanceled(true);
        bucketItems.add(index, bucketItem);
        refresh();
    }

    private void moveItem(BucketItem bucketItem, int index) {
        int itemIndex = bucketItems.indexOf(bucketItem);
        BucketItem temp = bucketItems.remove(itemIndex);
        bucketItems.add(index, temp);
    }

    public void reloadWithFilter(int filterId) {
        switch (filterId) {
            case R.id.action_show_all:
                showToDO = true;
                showCompleted = true;
                break;
            case R.id.action_show_to_do:
                showToDO = true;
                showCompleted = false;
                break;
            case R.id.action_show_completed:
                showToDO = false;
                showCompleted = true;
                break;
        }
        refresh();
    }

    public void itemMoved(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        BucketOrderModel orderModel = new BucketOrderModel();
        orderModel.setPosition(toPosition);

        dreamSpiceManager.execute(new ReorderBucketItemCommand(
                        bucketItems.get(fromPosition).getId(),
                        orderModel),
                new RequestListener<JsonObject>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        view.informUser(context.getString(R.string.smth_went_wrong));
                        refresh();
                    }

                    @Override
                    public void onRequestSuccess(JsonObject jsonObject) {
                        final BucketItem item = bucketItems.remove(fromPosition);
                        bucketItems.add(toPosition, item);
                        db.saveBucketList(bucketItems, type.name());
                    }
                });
    }

    public void addToBucketList(String title) {
        BucketPostItem bucketPostItem = new BucketPostItem(type.getName(), title, BucketItem.NEW);
        loadBucketItem(bucketPostItem);
    }

    private void loadBucketItem(BucketPostItem bucketPostItem) {
        dreamSpiceManager.execute(new AddBucketItemCommand(bucketPostItem),
                new RequestListener<BucketItem>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        //nothing to do here
                    }

                    @Override
                    public void onRequestSuccess(BucketItem bucketItem) {
                        bucketItems.add(0, bucketItem);
                        db.saveBucketList(bucketItems, type.name());

                        view.getAdapter().addItem(0, bucketItem);
                        view.getAdapter().notifyDataSetChanged();
                    }
                });
    }

    public boolean isShowToDO() {
        return showToDO;
    }

    public boolean isShowCompleted() {
        return showCompleted;
    }

    @Override
    public void destroyView() {
        super.destroyView();
        eventBus.unregister(this);
    }

    public AutoCompleteAdapter.Loader getSuggestionLoader() {
        return new SuggestionLoader(type, dreamSpiceManager, api);
    }

    public interface View extends Presenter.View {
        BaseArrayListAdapter getAdapter();

        void showUndoBar(android.view.View.OnClickListener clickListener);

        void startLoading();

        void finishLoading();

        void showDetailsContainer();
    }
}
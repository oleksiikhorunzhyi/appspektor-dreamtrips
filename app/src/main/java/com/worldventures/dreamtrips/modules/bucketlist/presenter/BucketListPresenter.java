package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.app.Activity;
import android.os.Bundle;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
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
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketTabChangedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketOrderModel;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketStatusItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.AutoCompleteAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.SuggestionLoader;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import icepick.Icicle;

public class BucketListPresenter extends Presenter<BucketListPresenter.View> {

    private static final int DELETION_DELAY = 3500;

    @Inject
    Activity activity;
    @Inject
    protected SnappyRepository db;
    @Inject
    protected DreamTripsApi api;
    @Inject
    protected Prefs prefs;

    private BucketTabsPresenter.BucketType type;

    @Icicle
    boolean showToDO = true;
    @Icicle
    boolean showCompleted = true;
    @Icicle
    BucketItem currentItem;

    private List<BucketItem> bucketItems = new ArrayList<>();

    private BucketHelper bucketHelper;
    private WeakHandler weakHandler;

    public BucketListPresenter(BucketTabsPresenter.BucketType type) {
        super();
        this.type = type;
        bucketHelper = new BucketHelper();
        weakHandler = new WeakHandler();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        TrackingHelper.bucketList(getUserId());
    }

    @Override
    public void dropView() {
        activity = null;
        super.dropView();
    }

    public void loadBucketItems() {
        if (isConnected()) {
            view.startLoading();
            doRequest(new GetBucketListQuery(prefs, db, type),
                    result -> {
                        view.finishLoading();
                        addItems(result);
                    }, exception -> {
                        view.finishLoading();
                        addItems(Collections.emptyList());
                        handleError(exception);
                    });
        } else {
            addItems(db.readBucketList(type.name()));
        }
    }

    public void trackAddStart() {
        TrackingHelper.bucketAddStart(type.name);
    }

    public void trackAddFinish() {
        TrackingHelper.bucketAddFinish(type.name);
    }

    private void addItems(Collection<? extends BucketItem> result) {
        bucketItems.clear();
        bucketItems.addAll(result);
        refresh();
    }

    private void refresh() {
        List<BucketItem> filteredItems = new ArrayList<>();
        if (bucketItems.isEmpty()) {
            currentItem = null;
        } else {
            if (showToDO) {
                Collection<BucketItem> toDo = Queryable.from(bucketItems)
                        .filter((bucketItem) -> !bucketItem.isDone())
                        .toList();
                filteredItems.addAll(toDo);
            }
            view.putCategoryMarker(filteredItems.size());
            if (showCompleted) {
                Collection<BucketItem> done = Queryable.from(bucketItems)
                        .filter((bucketItem) -> bucketItem.isDone())
                        .toList();
                filteredItems.addAll(done);
            }
            //
            if (filteredItems.isEmpty()) {
                currentItem = null;
            } else if (!filteredItems.contains(currentItem)) {
                currentItem = filteredItems.get(0);
            }
        }
        view.getAdapter().setItems(filteredItems);
        openDetailsIfNeeded(currentItem);
    }

    public void onEvent(BucketTabChangedEvent event) {
        if (type.equals(event.type)) {
            // when tab change we need to wait, till pager settles down
            weakHandler.postDelayed(() -> openDetailsIfNeeded(currentItem), 150L);
        }
    }

    public void onEvent(BucketItemClickedEvent event) {
        if (!bucketItems.contains(event.getBucketItem())) return;
        //
        eventBus.cancelEventDelivery(event);
        //
        currentItem = event.getBucketItem();
        openDetails(currentItem);
    }

    public void onEvent(BucketItemUpdatedEvent event) {
        if (isTypeCorrect(event.getBucketItem().getType())) {
            addItems(db.readBucketList(type.name()));
        }
    }

    public void onEvent(BucketItemAddedEvent event) {
        if (!bucketItems.contains(event.getBucketItem())
                && isTypeCorrect(event.getBucketItem().getType())) {
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
        if (!bucketItems.isEmpty() && isTypeCorrect(event.getBucketItem().getType())) {
            BucketItem bucketItem = event.getBucketItem();

            int position = bucketItem.isDone() ?
                    bucketItems.indexOf(Queryable.from(bucketItems).first(BucketItem::isDone)) : 0;
            moveItem(bucketItem, position);

            BucketStatusItem bucketStatusItem = new BucketStatusItem(bucketItem.getStatus());

            doRequest(new MarkBucketItemCommand(event.getBucketItem().getId(), bucketStatusItem),
                    item -> {
                        db.saveBucketList(bucketItems, type.name());
                    }, exception -> {
                        bucketItems.get(bucketItems.indexOf(bucketItem)).setDone(!bucketItem.isDone());
                        refresh();
                    });

            bucketItems.get(bucketItems.indexOf(bucketItem)).setDone(bucketItem.isDone());
            refresh();
        }
    }

    public void onEvent(DeleteBucketItemEvent event) {
        if (bucketItems.isEmpty() || !isTypeCorrect(event.getBucketItem().getType())) return;
        //
        eventBus.cancelEventDelivery(event);

        int index = bucketItems.indexOf(event.getBucketItem());
        bucketItems.remove(event.getBucketItem());
        //
        if (currentItem.equals(event.getBucketItem())) {
            if (bucketItems.isEmpty()) currentItem = null;
            else {
                currentItem = index == bucketItems.size() ?
                        bucketItems.get(index - 1) :
                        bucketItems.get(index);
            }
        }
        refresh();
        // make request
        DeleteBucketItemCommand request = deleteDelayed(event.getBucketItem().getId());
        view.showUndoBar((v) -> undo(event.getBucketItem(), index, request));
    }

    private boolean isTypeCorrect(String bucketType) {
        return bucketType.equalsIgnoreCase(type.getName());
    }

    private void openDetailsIfNeeded(BucketItem item) {
        if (!view.isTabletLandscape() || !view.isVisibleOnScreen()) return;
        //
        if (item != null) openDetails(item);
        else {
            fragmentCompass.setContainerId(R.id.container_main);
            view.hideDetailsContainer();
        }
    }

    private void openDetails(BucketItem bucketItem) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BucketActivity.EXTRA_TYPE, type);
        bundle.putSerializable(BucketActivity.EXTRA_ITEM, bucketItem);
        fragmentCompass.removeDetailed();
        if (view.isTabletLandscape()) {
            view.showDetailsContainer();
            fragmentCompass.disableBackStack();
            fragmentCompass.setContainerId(R.id.container_details_fullscreen);
            fragmentCompass.replace(Route.DETAIL_BUCKET, bundle);
        } else {
            activityRouter.openBucketItemDetails(bundle);
        }
        // set selected
        Queryable.from(bucketItems).forEachR(item -> {
            item.setSelected(bucketItem.equals(item));
        });
        view.getAdapter().notifyDataSetChanged();
    }

    public void addPopular() {
        activityRouter.openBucketListPopularActivity(type);
    }

    private DeleteBucketItemCommand deleteDelayed(int id) {
        DeleteBucketItemCommand request = new DeleteBucketItemCommand(id, DELETION_DELAY);
        doRequest(request, obj -> {
            db.saveBucketList(bucketItems, type.name());
        });
        return request;
    }

    private void undo(BucketItem bucketItem, int index, DeleteBucketItemCommand request) {
        request.setCanceled(true);
        bucketItems.add(index, bucketItem);
        currentItem = bucketItem;
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
            default:
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

        doRequest(new ReorderBucketItemCommand(bucketItems.get(fromPosition).getId(),
                orderModel), jsonObject -> {
            final BucketItem item = bucketItems.remove(fromPosition);
            bucketItems.add(toPosition, item);
            db.saveBucketList(bucketItems, type.name());
        });
    }

    public void addToBucketList(String title) {
        BucketPostItem bucketPostItem = new BucketPostItem(type.getName(), title, BucketItem.NEW);
        addBucketItem(bucketPostItem);
    }

    private void addBucketItem(BucketPostItem bucketPostItem) {
        doRequest(new AddBucketItemCommand(bucketPostItem), bucketItem -> {
            bucketItems.add(0, bucketItem);
            db.saveBucketList(bucketItems, type.name());

            trackAddFinish();
            view.getAdapter().addItem(0, bucketItem);
            view.getAdapter().notifyDataSetChanged();

            bucketHelper.notifyItemAddedToBucket(activity, bucketItem);
        });
    }

    public boolean isShowToDO() {
        return showToDO;
    }

    public boolean isShowCompleted() {
        return showCompleted;
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

        void hideDetailsContainer();

        void putCategoryMarker(int position);
    }
}
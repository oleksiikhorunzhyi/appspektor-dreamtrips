package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.app.Activity;
import android.os.Bundle;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.BucketItemsLoadedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemClickedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketTabChangedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.AutoCompleteAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.SuggestionLoader;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import icepick.Icicle;

public class BucketListPresenter extends Presenter<BucketListPresenter.View> {

    @Inject
    Activity activity;
    @Inject
    DreamTripsApi api;

    @Inject
    BucketItemManager bucketItemManager;

    private BucketTabsPresenter.BucketType type;

    @Icicle
    boolean showToDO = true;
    @Icicle
    boolean showCompleted = true;
    @Icicle
    BucketItem currentItem;

    private List<BucketItem> bucketItems = new ArrayList<>();

    private WeakHandler weakHandler;

    public BucketListPresenter(BucketTabsPresenter.BucketType type) {
        super();
        this.type = type;
        weakHandler = new WeakHandler();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        TrackingHelper.bucketList(getAccountUserId());
        view.startLoading();
    }

    public void onEvent(BucketItemsLoadedEvent event) {
        showItems();
    }

    private void showItems() {
        view.finishLoading();
        bucketItems = bucketItemManager.getBucketItems(type);
        refresh();
    }

    private void refresh() {
        fillWithItems();
        openDetailsIfNeeded(currentItem);
    }

    private void refresh(List<BucketItem> tempItems) {
        bucketItems = tempItems;
        refresh();
    }

    private void fillWithItems() {
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
                        .filter(BucketItem::isDone)
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
        view.checkEmpty(bucketItems.size());
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

    public void onEventMainThread(BucketItemUpdatedEvent event) {
        if (isTypeCorrect(event.getBucketItem().getType())) {
            showItems();
        }
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
        bundle.putInt(BucketActivity.EXTRA_ITEM, bucketItem.getId());
        fragmentCompass.removeDetailed();
        if (view.isTabletLandscape()) {
            view.showDetailsContainer();
            fragmentCompass.disableBackStack();
            fragmentCompass.setContainerId(R.id.container_details_fullscreen);
            fragmentCompass.replace(Route.DETAIL_BUCKET, bundle);
        } else {
            activityRouter.openBucketItemDetails(type, bucketItem.getId());
        }
        // set selected
        Queryable.from(bucketItems).forEachR(item ->
                item.setSelected(bucketItem.equals(item)));
        view.getAdapter().notifyDataSetChanged();
    }

    public void addPopular() {
        activityRouter.openBucketListPopularActivity(type);
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

    public void onEvent(MarkBucketItemDoneEvent event) {
        if (isTypeCorrect(event.getBucketItem().getType())) {
            eventBus.cancelEventDelivery(event);
            markAsDone(event.getBucketItem());
        }
    }

    private void markAsDone(BucketItem bucketItem) {
        refresh(bucketItemManager.markBucketItemAsDone(bucketItem, type, exception -> {
            bucketItems = bucketItemManager.getBucketItems(type);
            refresh();
        }));
    }

    public void itemMoved(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        refresh(bucketItemManager.moveItem(fromPosition, toPosition, type, spiceException -> {
            refresh();
            handleError(spiceException);
        }));
    }

    public void addToBucketList(String title) {
        bucketItemManager.addBucketItem(title, type, bucketItem -> {
            bucketItems.add(0, bucketItem);
            view.getAdapter().addItem(0, bucketItem);
            view.getAdapter().notifyDataSetChanged();
        }, this::handleError);
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
        BaseArrayListAdapter<BucketItem> getAdapter();

        void startLoading();

        void finishLoading();

        void showDetailsContainer();

        void hideDetailsContainer();

        void putCategoryMarker(int position);

        void checkEmpty(int count);
    }
}
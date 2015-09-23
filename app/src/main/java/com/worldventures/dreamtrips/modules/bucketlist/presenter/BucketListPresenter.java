package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.utils.events.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.BucketItemsLoadedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.AutoCompleteAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.SuggestionLoader;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import icepick.State;


public class BucketListPresenter extends Presenter<BucketListPresenter.View> {

    @Inject
    DreamTripsApi api;

    BucketItemManager bucketItemManager;

    private BucketItem.BucketType type;

    @State
    boolean showToDO = true;
    @State
    boolean showCompleted = true;

    BucketItem currentItem;

    private List<BucketItem> bucketItems = new ArrayList<>();

    public BucketListPresenter(BucketItem.BucketType type, ObjectGraph objectGraph) {
        super();
        this.type = type;
        bucketItemManager = objectGraph.get(getBucketItemManagerClass());
    }

    @NonNull
    protected Class<? extends BucketItemManager> getBucketItemManagerClass() {
        return BucketItemManager.class;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        TrackingHelper.bucketList(getAccountUserId());
        view.startLoading();
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
        view.checkEmpty(filteredItems.size());
    }

    public void itemClicked(BucketItem bucketItem) {
        if (!isTypeCorrect(bucketItem.getType()) &&
                !bucketItems.contains(bucketItem)) return;

        currentItem = bucketItem;
        openDetails(currentItem);
    }

    public void onEventMainThread(BucketItemUpdatedEvent event) {
        if (isTypeCorrect(event.getBucketItem().getType())) {
            showItems();
        }
    }

    public void onEvent(BucketItemsLoadedEvent event) {
        showItems();
    }

    public void onEvent(MarkBucketItemDoneEvent event) {
        if (isTypeCorrect(event.getBucketItem().getType())) {
            eventBus.cancelEventDelivery(event);
            markAsDone(event.getBucketItem());
        }
    }

    private boolean isTypeCorrect(String bucketType) {
        return bucketType.equalsIgnoreCase(type.getName());
    }

    private void openDetailsIfNeeded(BucketItem item) {
        if (view == null || !view.isTabletLandscape()) return;
        //
        if (item != null) openDetails(item);
        else {
            view.hideDetailContainer();
        }
    }

    private void openDetails(BucketItem bucketItem) {
        BucketBundle bundle = new BucketBundle();
        bundle.setType(type);
        bundle.setBucketItemId(bucketItem.getUid());
        view.openDetails(bundle);
        // set selected
        Queryable.from(bucketItems).forEachR(item ->
                item.setSelected(bucketItem.equals(item)));

        view.getAdapter().notifyDataSetChanged();
    }

    public void popularClicked() {
        BucketBundle bundle = new BucketBundle();
        bundle.setType(type);
        view.openPopular(bundle);
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
            view.getAdapter().addItem(0, bucketItem);
            view.getAdapter().notifyDataSetChanged();
            if (bucketItems.size() == 1) currentItem = bucketItem;
            openDetailsIfNeeded(bucketItem);
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

        void hideDetailContainer();

        void putCategoryMarker(int position);

        void checkEmpty(int count);

        void openDetails(BucketBundle args);

        void openPopular(BucketBundle args);
    }
}
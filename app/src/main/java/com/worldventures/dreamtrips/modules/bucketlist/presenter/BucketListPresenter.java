package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.JsonObject;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.core.utils.events.BucketItemAddedEvent;
import com.worldventures.dreamtrips.core.utils.events.BucketItemClickedEvent;
import com.worldventures.dreamtrips.core.utils.events.DeleteBucketItemEvent;
import com.worldventures.dreamtrips.core.utils.events.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.modules.bucketlist.api.AddBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetBucketListQuery;
import com.worldventures.dreamtrips.modules.bucketlist.api.MarkBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.ReorderBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketHeader;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketOrderModel;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BucketListPresenter extends Presenter<BucketListPresenter.View> {

    @Inject
    public Context context;
    @Inject
    public SnappyRepository db;
    @Global
    @Inject
    public EventBus eventBus;
    @Inject
    public Prefs prefs;

    private BucketTabsFragment.Type type;
    private boolean showToDO = true;
    private boolean showCompleted = true;
    private List<BucketItem> bucketItems = new ArrayList<BucketItem>();

    public BucketListPresenter(View view, BucketTabsFragment.Type type) {
        super(view);
        this.type = type;
    }

    @Override
    public void init() {
        super.init();
        AdobeTrackingHelper.bucketList(getUserId());
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
            dreamSpiceManager.execute(new GetBucketListQuery(prefs, db, type), new RequestListener<ArrayList<BucketItem>>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    view.informUser("Could not load " + type.getName());
                }

                @Override
                public void onRequestSuccess(ArrayList<BucketItem> result) {
                    view.finishLoading();
                    addItems(result);
                }
            });
        } else {
            try {
                addItems(db.readBucketList(type.name()));
            } catch (Exception e) {
                Log.e(BucketListPresenter.class.getSimpleName(), "", e);
            }
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
            Collection<BucketItem> toDo = Queryable.from(bucketItems).filter((bucketItem) -> !bucketItem.isDone()).toList();
            Collection<BucketItem> done = Queryable.from(bucketItems).filter((bucketItem) -> bucketItem.isDone()).toList();
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
        if (!bucketItems.contains(event.getBucketItem())
                && event.getBucketItem().getType().equalsIgnoreCase(type.getName())) {
            eventBus.cancelEventDelivery(event);
            openDetails(event.getBucketItem());
        }
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
        if (bucketItems.size() > 0 && event.getBucketItem().getType().equalsIgnoreCase(type.getName())) {
            eventBus.cancelEventDelivery(event);
            BucketItem bucketItem = event.getBucketItem();

            moveItem(bucketItem, bucketItem.isDone()
                    ? bucketItems.indexOf(Queryable.from(bucketItems).first((item) -> item.isDone()))
                    : 0);

            BucketPostItem bucketPostItem = new BucketPostItem();
            bucketPostItem.setStatus(bucketItem.getStatus());

            Log.d("TAG_BucketListPM", "Receivent mark as done event");

            dreamSpiceManager.execute(new MarkBucketItemCommand(event.getBucketItem().getId(), bucketPostItem), new RequestListener<BucketItem>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    view.informUser(spiceException.getMessage());
                    Log.d("TAG_BucketListPM", spiceException.getMessage());
                }

                @Override
                public void onRequestSuccess(BucketItem jsonObject) {
                    Log.d("TAG_BucketListPM", "Item marked as done");
                }
            });

            bucketItems.get(bucketItems.indexOf(bucketItem)).setDone(bucketItem.isDone());
            db.saveBucketList(bucketItems, type.name());
            refresh();
        }
    }

    public void onEvent(DeleteBucketItemEvent event) {
        if (bucketItems.size() > 0 && event.getBucketItem().getType().equalsIgnoreCase(type.getName())) {
            eventBus.cancelEventDelivery(event);

            Log.d("TAG_BucketListPM", "Receivent delete event");

            int index = bucketItems.indexOf(event.getBucketItem());
            bucketItems.remove(event.getBucketItem());
            refresh();

            DeleteBucketItemCommand request = hiden(event.getBucketItem().getId());
            view.showUndoBar((v) -> undo(event.getBucketItem(), index, request));
        }
    }

    private void openDetails(BucketItem bucketItem) {
        activityRouter.openBucketItemEditActivity(type, bucketItem);
    }

    public void addPopular() {
        activityRouter.openBucketListPopularActivity(type);
    }

    private DeleteBucketItemCommand hiden(int id) {
        DeleteBucketItemCommand request = new DeleteBucketItemCommand(id, 3500);
        dreamSpiceManager.execute(request, new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                view.informUser(spiceException.getMessage());
                Log.d("TAG_BucketListPM", spiceException.getMessage());
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                Log.d("TAG_BucketListPM", "Item deleted");
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

    @Override
    public void destroyView() {
        super.destroyView();
        eventBus.unregister(this);
    }

    public void itemMoved(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        Log.d("TAG_BucketListPM", "Syncing position from " + fromPosition + " to " + toPosition);
        BucketOrderModel orderModel = new BucketOrderModel();
        orderModel.setPosition(toPosition);

        dreamSpiceManager.execute(new ReorderBucketItemCommand(bucketItems.get(fromPosition).getId(), orderModel), new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                view.informUser(context.getString(R.string.smth_went_wrong));
                refresh();
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                Log.d("TAG_BucketListPM", "Synced position!");
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
        dreamSpiceManager.execute(new AddBucketItemCommand(bucketPostItem), new RequestListener<BucketItem>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
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

    public interface View extends Presenter.View {
        BaseArrayListAdapter getAdapter();

        void showUndoBar(android.view.View.OnClickListener clickListener);

        void startLoading();

        void finishLoading();
    }
}
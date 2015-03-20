package com.worldventures.dreamtrips.presentation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.JsonObject;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.bucket.BucketHeader;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.core.model.bucket.BucketOrderModel;
import com.worldventures.dreamtrips.core.model.bucket.BucketPostItem;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.utils.events.BucketItemAddedEvent;
import com.worldventures.dreamtrips.utils.events.DeleteBucketItemEvent;
import com.worldventures.dreamtrips.utils.events.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BucketListFragmentPM extends BasePresentation<BucketListFragmentPM.View> {
    private static final Handler handler = new Handler(Looper.getMainLooper());
    @Inject
    Context context;
    @Inject
    SnappyRepository db;
    @Global
    @Inject
    EventBus eventBus;
    @Inject
    Prefs prefs;
    private BucketTabsFragment.Type type;
    private boolean showToDO = true;
    private boolean showCompleted = true;
    private List<BucketItem> bucketItems = new ArrayList<BucketItem>();

    public BucketListFragmentPM(View view, BucketTabsFragment.Type type) {
        super(view);
        this.type = type;
    }

    @Override
    public void init() {
        super.init();
        AdobeTrackingHelper.bucketList(getUserId());
        eventBus.register(this);
    }

    public void loadBucketItems(boolean fromNetwork) {
        if (isConnected()) {
            view.startLoading();
            dreamSpiceManager.execute(new DreamTripsRequest.GetBucketList(prefs, db, type, fromNetwork), new RequestListener<ArrayList<BucketItem>>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    view.alert(spiceException.getMessage());
                }

                @Override
                public void onRequestSuccess(ArrayList<BucketItem> result) {
                    bucketItems.clear();
                    bucketItems.addAll(result);
                    fillWithItems();
                    view.finishLoading();
                }
            });
        } else {
            try {
                this.bucketItems.clear();
                this.bucketItems.addAll(db.readBucketList(type.name()));
                fillWithItems();
            } catch (Exception e) {
                Log.e(BucketListFragmentPM.class.getSimpleName(), "", e);
            }
        }
    }

    private void fillWithItems() {
        ArrayList<Object> result = new ArrayList<>();
        if (bucketItems.size() > 0) {
            Collection<BucketItem> toDo = Queryable.from(bucketItems).filter((bucketItem) -> !bucketItem.isDone()).toList();
            Collection<BucketItem> done = Queryable.from(bucketItems).filter((bucketItem) -> bucketItem.isDone()).toList();
            if (showToDO && toDo.size() > 0) {
                result.add(new BucketHeader(0, R.string.to_do));
                result.addAll(toDo);
            }
            if (showCompleted && done.size() > 0) {
                result.add(new BucketHeader(0, R.string.completed));
                result.addAll(done);
            }
        }
        view.getAdapter().setItems(result);
    }

    public void onEvent(BucketItemAddedEvent event) {
        if (!bucketItems.contains(event.getBucketItem())
                && event.getBucketItem().getType().equalsIgnoreCase(type.getName())) {
            if (event.getBucketItem().isDone()) {
                bucketItems.add(0, event.getBucketItem());
                fillWithItems();
            } else {
                bucketItems.add(0, event.getBucketItem());
                fillWithItems();
            }
            eventBus.cancelEventDelivery(event);
        }
    }

    public void addPopular() {
        activityRouter.openBucketListEditActivity(type, State.POPULAR_TAB_BUCKER);
    }

    public void onEvent(DeleteBucketItemEvent event) {
        if (bucketItems.size() > 0 && event.getBucketItem().getType().equalsIgnoreCase(type.getName())) {
            eventBus.cancelEventDelivery(event);

            Log.d("TAG_BucketListPM", "Receivent delete event");

            int index = bucketItems.indexOf(event.getBucketItem());
            bucketItems.remove(event.getBucketItem());
            fillWithItems();

            DreamTripsRequest.DeleteBucketItem request = hiden(event.getBucketItem().getId());
            view.showUndoBar((v) -> undo(event.getBucketItem(), index, request));
        }
    }

    private DreamTripsRequest.DeleteBucketItem hiden(int id) {
        DreamTripsRequest.DeleteBucketItem request = new DreamTripsRequest.DeleteBucketItem(id, 3500);
        dreamSpiceManager.execute(request, new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
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

    private void undo(BucketItem bucketItem, int index, DreamTripsRequest.DeleteBucketItem request) {
        request.setCanceled(true);
        bucketItems.add(index, bucketItem);
        fillWithItems();
    }

    public void onEvent(MarkBucketItemDoneEvent event) {
        if (bucketItems.size() > 0 && event.getBucketItem().getType().equalsIgnoreCase(type.getName())) {
            eventBus.cancelEventDelivery(event);
            BucketItem bucketItem = event.getBucketItem();

            BucketPostItem bucketPostItem = new BucketPostItem();
            bucketPostItem.setStatus(bucketItem.getStatus());

            Log.d("TAG_BucketListPM", "Receivent mark as done event");

            dreamSpiceManager.execute(new DreamTripsRequest.MarkBucketItem(event.getBucketItem().getId(), bucketPostItem), new RequestListener<BucketItem>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    view.alert(spiceException.getMessage());
                    Log.d("TAG_BucketListPM", spiceException.getMessage());
                }

                @Override
                public void onRequestSuccess(BucketItem jsonObject) {
                    Log.d("TAG_BucketListPM", "Item marked as done");
                }
            });

            bucketItems.get(bucketItems.indexOf(bucketItem)).setDone(bucketItem.isDone());
            db.saveBucketList(bucketItems, type.name());
            fillWithItems();
        }
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
        fillWithItems();
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

        final BucketItem item = bucketItems.remove(fromPosition);
        bucketItems.add(toPosition, item);

        db.saveBucketList(bucketItems, type.name());
        //syncPosition(item, toPosition);
    }

    private void syncPosition(BucketItem bucketItem, int to) {
        BucketOrderModel orderModel = new BucketOrderModel();
        orderModel.setId(bucketItem.getId());
        orderModel.setPosition(to);
        dreamSpiceManager.execute(new DreamTripsRequest.ReorderBucketItem(orderModel), new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                view.informUser(context.getString(R.string.smth_went_wrong));
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
            }
        });
    }

    public void addToBucketList(String title) {
        BucketPostItem bucketPostItem = new BucketPostItem(type.getName(), title, BucketItem.NEW);
        loadBucketItem(bucketPostItem);
    }

    public void loadBucketItem(BucketPostItem bucketPostItem) {
        dreamSpiceManager.execute(new DreamTripsRequest.AddBucketItem(bucketPostItem), new RequestListener<BucketItem>() {
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

    public interface View extends BasePresentation.View {
        BaseArrayListAdapter getAdapter();

        void showUndoBar(android.view.View.OnClickListener clickListener);

        void startLoading();

        void finishLoading();
    }
}
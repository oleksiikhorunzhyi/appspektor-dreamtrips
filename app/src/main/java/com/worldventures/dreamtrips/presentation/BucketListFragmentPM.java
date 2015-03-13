package com.worldventures.dreamtrips.presentation;

import android.content.Context;

import com.google.common.collect.Collections2;
import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.bucket.BucketHeader;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.core.model.bucket.BucketPostItem;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.utils.busevents.BucketItemAddedEvent;
import com.worldventures.dreamtrips.utils.busevents.DeleteBucketItemEvent;
import com.worldventures.dreamtrips.utils.busevents.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BucketListFragmentPM extends BasePresentation<BucketListFragmentPM.View> {
    private BucketTabsFragment.Type type;

    public BucketListFragmentPM(View view, BucketTabsFragment.Type type) {
        super(view);
        this.type = type;
    }

    @Inject
    Context context;

    @Inject
    SnappyRepository db;

    @Global
    @Inject
    EventBus eventBus;

    @Inject
    Prefs prefs;

    private boolean showToDO = true;
    private boolean showCompleted = true;

    private List<BucketItem> bucketItems = new ArrayList<BucketItem>();

    @Override
    public void init() {
        super.init();
        AdobeTrackingHelper.bucketList(getUserId());
        eventBus.register(this);
        loadBucketItems(false);
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
                public void onRequestSuccess(ArrayList<BucketItem> bucketItems) {
                    BucketListFragmentPM.this.bucketItems = bucketItems;
                    fillWithItems();
                    view.finishLoading();
                }
            });
        } else {
            try {
                this.bucketItems = db.readBucketList(type.name());
                fillWithItems();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillWithItems() {
        ArrayList<Object> result = new ArrayList<>();
        if (bucketItems.size() > 0) {
            Collection<BucketItem> toDo = Collections2.filter(bucketItems, (bucketItem) -> !bucketItem.isDone());
            Collection<BucketItem> done = Collections2.filter(bucketItems, (bucketItem) -> bucketItem.isDone());
            if (showToDO && toDo.size() > 0) {
                result.add(new BucketHeader(bucketItems.size() + 1, R.string.to_do));
                result.addAll(toDo);
            }
            if (showCompleted && done.size() > 0) {
                result.add(new BucketHeader(bucketItems.size() + 2, R.string.completed));
                result.addAll(done);
            }
        }
        view.getAdapter().clear();
        view.getAdapter().addItems(result);
    }

    public void onEvent(BucketItemAddedEvent event) {
        if (event.getBucketItem().getType().equalsIgnoreCase(type.getName())) {
            bucketItems.add(event.getBucketItem());
            view.getAdapter().addItem(1, event.getBucketItem());
            view.getAdapter().notifyDataSetChanged();

        }
    }

    public void onEvent(DeleteBucketItemEvent event) {
        if (event.getBucketItem().getType().equalsIgnoreCase(type.getName())) {
            bucketItems.remove(event.getBucketItem());
            db.saveBucketList(bucketItems, type.name());
            dreamSpiceManager.execute(new DreamTripsRequest.DeleteBucketItem(event.getBucketItem().getId()), new RequestListener<JsonObject>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                }

                @Override
                public void onRequestSuccess(JsonObject jsonObject) {
                }
            });
        }
    }

    public void itemMoved(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final BucketItem item = bucketItems.remove(fromPosition);
        bucketItems.add(toPosition, item);

        db.saveBucketList(bucketItems, type.name());
    }

    public void onEvent(MarkBucketItemDoneEvent event) {
        if (event.getBucketItem().getType().equalsIgnoreCase(type.getName())) {
            db.saveBucketList(bucketItems, type.name());

            BucketPostItem bucketPostItem = new BucketPostItem();
            bucketPostItem.setStatus(event.getBucketItem().getStatus());
            dreamSpiceManager.execute(new DreamTripsRequest.MarkBucketItem(event.getBucketItem().getId(), bucketPostItem), new RequestListener<BucketItem>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                }

                @Override
                public void onRequestSuccess(BucketItem jsonObject) {

                }
            });

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

    public interface View extends BasePresentation.View {
        BaseArrayListAdapter getAdapter();

        void startLoading();

        void finishLoading();

        //void isTabletLandscape();
    }
}
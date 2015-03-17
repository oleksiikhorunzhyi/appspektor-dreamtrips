package com.worldventures.dreamtrips.presentation;

import android.content.Context;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.core.model.bucket.BucketPostItem;
import com.worldventures.dreamtrips.core.model.bucket.PopularBucketItem;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.utils.busevents.AddPressedEvent;
import com.worldventures.dreamtrips.utils.busevents.BucketItemAddedEvent;
import com.worldventures.dreamtrips.utils.busevents.DonePressedEvent;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by 1 on 03.03.15.
 */
public class BucketListPopularPM extends BasePresentation<BucketListPopularPM.View> {

    private BucketTabsFragment.Type type;

    @Inject
    Context context;

    @Global
    @Inject
    EventBus eventBus;

    @Inject
    SnappyRepository db;

    private List<BucketItem> realData = new ArrayList<>();

    private int lastId = 0;

    RoboSpiceAdapterController<PopularBucketItem> adapterController = new RoboSpiceAdapterController<PopularBucketItem>() {
        @Override
        public SpiceRequest<ArrayList<PopularBucketItem>> getRefreshRequest() {
            return new DreamTripsRequest.GetPopularLocation(type);
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<PopularBucketItem> items, SpiceException spiceException) {
            view.finishLoading();
        }
    };

    @Override
    public void init() {
        super.init();
        eventBus.register(this);
        try {
            realData.addAll(db.readBucketList(type.name()));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void resume() {
        super.resume();
        if (view.getAdapter().getCount() == 0) {
            adapterController.setSpiceManager(dreamSpiceManager);
            adapterController.setAdapter(view.getAdapter());
            adapterController.reload();
        }
    }

    public void onEvent(AddPressedEvent event) {
        add(event.getPopularBucketItem(), false, event.getPosition());
    }

    public void onEvent(DonePressedEvent event) {
        add(event.getPopularBucketItem(), true, event.getPosition());
    }

    private void add(PopularBucketItem popularBucketItem, boolean done, int position) {
        if (lastId != popularBucketItem.getId()) {
            lastId = popularBucketItem.getId();
            BucketPostItem bucketPostItem = new BucketPostItem(type.getName(),
                    popularBucketItem.getId(), done ? BucketItem.COMPLETED : BucketItem.NEW);
            dreamSpiceManager.execute(new DreamTripsRequest.AddBucketItem(bucketPostItem), new RequestListener<BucketItem>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    popularBucketItem.setLoading(false);
                    view.getAdapter().notifyDataSetChanged();
                }

                @Override
                public void onRequestSuccess(BucketItem bucketItem) {
                    view.getAdapter().remove(popularBucketItem);
                    view.getAdapter().notifyItemRemoved(position);
                    eventBus.post(new BucketItemAddedEvent(bucketItem));
                    realData.add(0, bucketItem);
                    db.saveBucketList(realData, type.name());
                }
            });
        }
    }

    public void reload() {
        adapterController.reload();
    }

    public BucketListPopularPM(View view, BucketTabsFragment.Type type) {
        super(view);
        this.type = type;
    }

    public interface View extends BasePresentation.View {
        BaseArrayListAdapter<PopularBucketItem> getAdapter();

        void startLoading();

        void finishLoading();
    }

}
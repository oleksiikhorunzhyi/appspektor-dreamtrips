package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.content.Context;
import android.util.Log;

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
import com.worldventures.dreamtrips.modules.common.presenter.BasePresentation;
import com.worldventures.dreamtrips.core.utils.events.AddPressedEvent;
import com.worldventures.dreamtrips.core.utils.events.BucketItemAddedEvent;
import com.worldventures.dreamtrips.core.utils.events.DonePressedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by 1 on 03.03.15.
 */
public class BucketListPopularPM extends BasePresentation<BucketListPopularPM.View> {

    @Inject
    Context context;
    @Global
    @Inject
    EventBus eventBus;
    @Inject
    SnappyRepository db;
    private BucketTabsFragment.Type type;
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
    private List<BucketItem> realData = new ArrayList<>();
    private int lastId = 0;

    public BucketListPopularPM(View view, BucketTabsFragment.Type type) {
        super(view);
        this.type = type;
    }

    @Override
    public void init() {
        super.init();
        eventBus.register(this);
        try {
            realData.addAll(db.readBucketList(type.name()));
        } catch (ExecutionException | InterruptedException e) {
            Log.e(BucketListPopularPM.class.getSimpleName(), "", e);
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

    public void onEventMainThread(AddPressedEvent event) {
        if (event.getPopularBucketItem().getType().equalsIgnoreCase(type.getName())) {
            add(event.getPopularBucketItem(), false, event.getPosition());
            eventBus.cancelEventDelivery(event);
        }
    }

    public void onEventMainThread(DonePressedEvent event) {
        if (event.getPopularBucketItem().getType().equalsIgnoreCase(type.getName())) {
            add(event.getPopularBucketItem(), true, event.getPosition());
            eventBus.cancelEventDelivery(event);
        }
    }

    private void add(PopularBucketItem popularBucketItem, boolean done, int position) {
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

    @Override
    public void destroyView() {
        eventBus.unregister(this);
        super.destroyView();
    }

    public void reload() {
        adapterController.reload();
    }

    public interface View extends BasePresentation.View {
        BaseArrayListAdapter<PopularBucketItem> getAdapter();

        void startLoading();

        void finishLoading();
    }

}
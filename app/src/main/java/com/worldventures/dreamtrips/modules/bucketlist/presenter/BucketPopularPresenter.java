package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.AddPressedEvent;
import com.worldventures.dreamtrips.core.utils.events.BucketItemAddedEvent;
import com.worldventures.dreamtrips.core.utils.events.DonePressedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.api.AddBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetPopularLocation;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BucketPopularPresenter extends Presenter<BucketPopularPresenter.View> {

    @Inject
    protected SnappyRepository db;

    private BucketTabsFragment.Type type;
    private List<BucketItem> realData = new ArrayList<>();

    protected RoboSpiceAdapterController<PopularBucketItem> adapterController = new RoboSpiceAdapterController<PopularBucketItem>() {
        @Override
        public SpiceRequest<ArrayList<PopularBucketItem>> getRefreshRequest() {
            return new GetPopularLocation(type);
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

    public BucketPopularPresenter(View view, BucketTabsFragment.Type type) {
        super(view);
        this.type = type;
    }

    @Override
    public void init() {
        super.init();
        realData.addAll(db.readBucketList(type.name()));
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
                popularBucketItem.getId());
        bucketPostItem.setStatus(done);
        dreamSpiceManager.execute(new AddBucketItemCommand(bucketPostItem), new RequestListener<BucketItem>() {
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

    public interface View extends Presenter.View {
        BaseArrayListAdapter<PopularBucketItem> getAdapter();

        void startLoading();

        void finishLoading();
    }

}
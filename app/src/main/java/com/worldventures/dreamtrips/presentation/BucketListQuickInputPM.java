package com.worldventures.dreamtrips.presentation;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.core.model.bucket.BucketPostItem;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.utils.busevents.BucketItemAddedEvent;
import com.worldventures.dreamtrips.utils.busevents.BucketItemReloadEvent;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by 1 on 26.02.15.
 */
public class BucketListQuickInputPM extends BasePresentation<BucketListQuickInputPM.View> {

    private BucketTabsFragment.Type type;

    private List<BucketPostItem> data = new ArrayList<>();
    private List<BucketItem> realData = new ArrayList<>();

    @Inject
    SnappyRepository db;

    @Global
    @Inject
    EventBus eventBus;

    public BucketListQuickInputPM(View view, BucketTabsFragment.Type type) {
        super(view);
        this.type = type;
    }


    @Override
    public void init() {
        super.init();
        try {
            realData.addAll(db.readBucketList(type.name()));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        eventBus.register(this);
    }

    public void onEvent(BucketItemReloadEvent event) {
        loadBucketItem(event.getBucketPostItem());
    }

    public void frameClicked() {
        fragmentCompass.pop();
    }

    public void addToBucketList(String title) {
        BucketPostItem bucketPostItem =  new BucketPostItem(type.getName(), title, BucketItem.NEW);

        data.add(0, bucketPostItem);
        view.getAdapter().addItem(bucketPostItem);
        view.getAdapter().notifyDataSetChanged();
        loadBucketItem(bucketPostItem);
    }

    public void loadBucketItem(BucketPostItem bucketPostItem) {
        bucketPostItem.setLoaded(false);
        bucketPostItem.setError(false);
        view.getAdapter().notifyDataSetChanged();

        dreamSpiceManager.execute(new DreamTripsRequest.AddBucketItem(bucketPostItem), new RequestListener<BucketItem>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                bucketPostItem.setError(true);
                bucketPostItem.setLoaded(false);
                view.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onRequestSuccess(BucketItem bucketItem) {
                bucketPostItem.setLoaded(true);
                eventBus.post(new BucketItemAddedEvent(bucketItem));
                view.getAdapter().notifyDataSetChanged();
                realData.add(0, bucketItem);
                db.saveBucketList(realData, type.name());
            }
        });
    }

    public interface View extends BasePresentation.View {
        BaseArrayListAdapter getAdapter();
    }
}

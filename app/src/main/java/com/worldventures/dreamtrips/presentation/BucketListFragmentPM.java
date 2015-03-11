package com.worldventures.dreamtrips.presentation;

import android.content.Context;

import com.google.common.collect.Collections2;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.bucket.BucketHeader;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.utils.busevents.DeleteBucketItemEvent;
import com.worldventures.dreamtrips.utils.busevents.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BucketListFragmentPM extends BasePresentation {
    private CollectionController<Object> adapterController;
    private BucketTabsFragment.Type type;

    public BucketListFragmentPM(View view, BucketTabsFragment.Type type) {
        super(view);
        this.type = type;
    }

    @Inject
    LoaderFactory loaderFactory;

    @Inject
    Context context;

    @Inject
    SnappyRepository db;

    @Global
    @Inject
    EventBus eventBus;

    private boolean showToDO = true;
    private boolean showCompleted = true;

    private List<BucketItem> bucketItems = new ArrayList<BucketItem>();

    @Override
    public void init() {
        super.init();
        AdobeTrackingHelper.bucketList(getUserId());
        this.adapterController = loaderFactory.create(type.ordinal(), (context, params) -> {
            ArrayList<Object> result = new ArrayList<>();

            try {
                List temp = db.readBucketList(type.name());
                bucketItems.clear();
                bucketItems.addAll(temp);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
            return result;
        });
        eventBus.register(this);
    }


    public void onEvent(DeleteBucketItemEvent event) {
        bucketItems.remove(event.getBucketItem());
        db.saveBucketList(bucketItems, type.name());
    }

    public void itemMoved(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final BucketItem item = bucketItems.remove(fromPosition);
        bucketItems.add(toPosition, item);

        db.saveBucketList(bucketItems, type.name());
    }

    public void onEvent(MarkBucketItemDoneEvent markBucketItemDoneEvent) {
        db.saveBucketList(bucketItems, type.name());
        adapterController.reload();
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
        adapterController.reload();
    }

    public CollectionController<Object> getAdapterController() {
        return adapterController;
    }


}
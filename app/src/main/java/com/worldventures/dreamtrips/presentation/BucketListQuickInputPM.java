package com.worldventures.dreamtrips.presentation;

import com.snappydb.DB;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * Created by 1 on 26.02.15.
 */
public class BucketListQuickInputPM extends BasePresentation<BasePresentation.View> {

    private CollectionController<BucketItem> adapterController;
    private BucketTabsFragment.Type type;


    private List<BucketItem> data = new ArrayList<>();
    private List<BucketItem> realData = new ArrayList<>();

    @Inject
    LoaderFactory loaderFactory;

    @Inject
    SnappyRepository db;


    public BucketListQuickInputPM(View view, BucketTabsFragment.Type type) {
        super(view);
        this.type = type;
    }

    public CollectionController<BucketItem> getAdapterController() {
        return adapterController;
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

        this.adapterController = loaderFactory.create(0, (context, params) -> {
            return data;
        });
    }

    public void addToBucketList(String title) {
        BucketItem bucketItem =  new BucketItem();
        bucketItem.setName(title);
        bucketItem.setId(realData.size());

        data.add(0, bucketItem);
        adapterController.reload();

        realData.add(0, bucketItem);
        db.saveBucketList(realData, type.name());
    }
}

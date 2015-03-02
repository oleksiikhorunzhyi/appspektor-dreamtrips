package com.worldventures.dreamtrips.presentation;

import com.snappydb.DB;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.utils.SnappyUtils;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by 1 on 26.02.15.
 */
public class BucketListQuickInputPM extends BasePresentation<BasePresentation.View> {

    private CollectionController<Object> adapterController;
    private List<BucketItem> data = new ArrayList<>();
    private String type;

    @Inject
    LoaderFactory loaderFactory;

    @Inject
    SnappyRepository db;

    public BucketListQuickInputPM(View view, String type) {
        super(view);
        this.type = type;
    }

    public CollectionController<Object> getAdapterController() {
        return adapterController;
    }

    @Override
    public void init() {
        super.init();
        this.adapterController = loaderFactory.create(0, (context, params) -> {
            ArrayList<Object> result = new ArrayList<>();
            result.addAll(data);
            return result;
        });
    }

    public void addToBucketList(String title) {
        BucketItem bucketItem =  new BucketItem();
        bucketItem.setName(title);
        db.addBucketItem(bucketItem, type);
        data.add(0, bucketItem);
        adapterController.reload();
    }
}

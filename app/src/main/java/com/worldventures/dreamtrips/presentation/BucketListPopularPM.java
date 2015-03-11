package com.worldventures.dreamtrips.presentation;

import android.content.Context;

import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.worldventures.dreamtrips.core.model.bucket.BucketPopularItem;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by 1 on 03.03.15.
 */
public class BucketListPopularPM extends BasePresentation {

    private CollectionController<Object> adapterController;
    private BucketTabsFragment.Type  type;

    @Inject
    LoaderFactory loaderFactory;

    @Inject
    Context context;

    @Inject
    SnappyRepository db;


    public BucketListPopularPM(View view, BucketTabsFragment.Type  type) {
        super(view);
        this.type = type;
    }

    @Override
    public void init() {
        super.init();
        this.adapterController = loaderFactory.create(type.ordinal(), (context, params) -> {
            ArrayList<Object> result = new ArrayList<>();

            result.add(new BucketPopularItem());
            result.add(new BucketPopularItem());
            result.add(new BucketPopularItem());

            return result;
        });

    }

    public CollectionController<Object> getAdapterController() {
        return adapterController;
    }

}

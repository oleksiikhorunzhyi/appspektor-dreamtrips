package com.worldventures.dreamtrips.presentation;

import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by 1 on 26.02.15.
 */
public class BucketListQuickInputPM extends BasePresentation<BasePresentation.View> {
    private CollectionController<Object> adapterController;

    @Inject
    LoaderFactory loaderFactory;

    public BucketListQuickInputPM(View view) {
        super(view);
    }

    public CollectionController<Object> getAdapterController() {
        return adapterController;
    }

    @Override
    public void init() {
        super.init();
        this.adapterController = loaderFactory.create(0, (context, params) -> {
            ArrayList<Object> result = new ArrayList<>();
            return result;
        });
    }

    public void addToBucketList() {

    }
}

package com.worldventures.dreamtrips.presentation;

import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.ContentLoader;
import com.techery.spares.loader.LoaderFactory;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.SuccessStory;

import java.util.List;

import javax.inject.Inject;

public class SuccessStoresTabPM extends BasePresentation {
    public SuccessStoresTabPM(View view) {
        super(view);
    }

    @Inject
    DreamTripsApi api;

    @Inject
    LoaderFactory loaderFactory;

    @Override
    public void resume() {
        api.getSuccessStores();
    }

    public ContentLoader<List<SuccessStory>> getStoresLoader() {
        return tripsController;
    }

    private CollectionController<SuccessStory> tripsController;


    @Override
    public void init() {
        super.init();
        this.tripsController = loaderFactory.create(0, (context, params) -> api.getSuccessStores());
    }

    public void reload() {
        tripsController.reload();
    }
}


package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.ContentLoader;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.utils.busevents.OnSuccessStoryCellClickEvent;
import com.worldventures.dreamtrips.utils.busevents.SuccessStoryItemClickEvent;
import com.worldventures.dreamtrips.view.fragment.StaticInfoFragment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.greenrobot.event.EventBus;

public class SuccessStoresTabPM extends BasePresentation<SuccessStoresTabPM.View> {
    public SuccessStoresTabPM(View view) {
        super(view);
    }

    @Inject
    DreamTripsApi api;

    @Inject
    LoaderFactory loaderFactory;
    @Inject
    @Named("details")
    FragmentCompass detailsCompass;

    @Inject
    @Global
    EventBus eventBus;

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
        eventBus.register(this);
        this.tripsController = loaderFactory.create(0, (context, params) -> api.getSuccessStores());
        boolean isLandTablet = view.isLandscape() && view.isTablet();
        view.setDetailsContainerVisibility(isLandTablet);
    }

    public void reload() {
        tripsController.reload();
    }

    public void onEvent(OnSuccessStoryCellClickEvent event) {
        handleClick(event.getModelObject(), event.getPosition());
    }

    private void handleClick(SuccessStory successStory, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(StaticInfoFragment.BundleUrlFragment.URL_EXTRA, successStory.getUrl());
        if (view.isLandscape() && view.isTablet()) {
            detailsCompass.replace(State.BUNDLE_URL_WEB, bundle);
            eventBus.post(new SuccessStoryItemClickEvent(position));
        } else {
            fragmentCompass.add(State.BUNDLE_URL_WEB, bundle);
        }
    }

    public static interface View extends BasePresentation.View {

        boolean isTablet();

        boolean isLandscape();

        void setDetailsContainerVisibility(boolean b);
    }
}


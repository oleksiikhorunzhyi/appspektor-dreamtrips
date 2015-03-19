package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.utils.busevents.OnSuccessStoryCellClickEvent;
import com.worldventures.dreamtrips.utils.busevents.SuccessStoryItemSelectedEvent;
import com.worldventures.dreamtrips.utils.busevents.SuccessStoryLikedEvent;
import com.worldventures.dreamtrips.view.fragment.reptools.SuccessStoriesDetailsFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.greenrobot.event.EventBus;

public class SuccessStoriesListFragmentPM extends BasePresentation<SuccessStoriesListFragmentPM.View> {

    @Inject
    @Named("details")
    FragmentCompass detailsCompass;
    @Inject
    @Global
    EventBus eventBus;


    boolean onlyFavorites = false;

    RoboSpiceAdapterController<SuccessStory> adapterController = new RoboSpiceAdapterController<SuccessStory>() {
        @Override
        public SpiceRequest<ArrayList<SuccessStory>> getRefreshRequest() {
            return new DreamTripsRequest.GetSuccessStores() {
                @Override
                public ArrayList<SuccessStory> loadDataFromNetwork() throws Exception {
                    return performFiltering(super.loadDataFromNetwork());
                }
            };
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<SuccessStory> items, SpiceException spiceException) {
            view.finishLoading(items);
        }
    };

    private ArrayList<SuccessStory> performFiltering(ArrayList<SuccessStory> successStories) {
        ArrayList<SuccessStory> result = new ArrayList<>();
        if (isFilterFavorites()) {
            for (SuccessStory successStory : successStories) {
                if (successStory.isLiked()) result.add(successStory);
            }
        } else {
            result.addAll(successStories);
        }
        return result;
    }

    public SuccessStoriesListFragmentPM(View view) {
        super(view);
    }

    @Override
    public void resume() {
        adapterController.setSpiceManager(dreamSpiceManager);
        adapterController.setAdapter(view.getAdapter());
        adapterController.reload();
    }

    @Override
    public void init() {
        super.init();
        eventBus.register(this);
        boolean isLandTablet = view.isLandscape() && view.isTablet();
        view.setDetailsContainerVisibility(isLandTablet);

    }

    public void reload() {
        adapterController.reload();
    }

    public void onEvent(OnSuccessStoryCellClickEvent event) {
        handleListItemClick(event.getModelObject(), event.getPosition());
    }

    public void onEvent(SuccessStoryLikedEvent event) {
        reload();
    }


    private void handleListItemClick(SuccessStory successStory, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SuccessStoriesDetailsFragment.STORY, successStory);
        if (view.isLandscape() && view.isTablet()) {
            detailsCompass.replace(State.SUCCESS_STORES_DETAILS, bundle);
            eventBus.post(new SuccessStoryItemSelectedEvent(position));
        } else {
            activityRouter.openSuccessStoryDetails(successStory);
        }
    }

    public void reloadWithFilter(int filterId) {
        switch (filterId) {
            case R.id.action_show_all:
                onlyFavorites = false;
                break;
            case R.id.action_show_favorites:
                onlyFavorites = true;
                break;
        }
        reload();
    }

    @Override
    public void destroy() {
        eventBus.unregister(this);
        super.destroy();
    }

    public boolean isFilterFavorites() {
        return onlyFavorites;
    }

    public static interface View extends BasePresentation.View {

        boolean isTablet();

        boolean isLandscape();

        void setDetailsContainerVisibility(boolean b);

        IRoboSpiceAdapter<SuccessStory> getAdapter();

        void finishLoading(List<SuccessStory> items);

        void startLoading();

        void showOnlyFavorites(boolean onlyFavorites);
    }
}


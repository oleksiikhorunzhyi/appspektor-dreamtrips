package com.worldventures.dreamtrips.modules.reptools.presenter;

import android.os.Bundle;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.OnSuccessStoryCellClickEvent;
import com.worldventures.dreamtrips.core.utils.events.SuccessStoryItemSelectedEvent;
import com.worldventures.dreamtrips.core.utils.events.SuccessStoryLikedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.reptools.api.successstories.GetSuccessStoriesQuery;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoriesDetailsFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.greenrobot.event.EventBus;

public class SuccessStoriesListPresenter extends Presenter<SuccessStoriesListPresenter.View> {

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
            return new GetSuccessStoriesQuery() {
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

    public SuccessStoriesListPresenter(View view) {
        super(view);
    }

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

    @Override
    public void resume() {
        adapterController.setSpiceManager(dreamSpiceManager);
        adapterController.setAdapter(view.getAdapter());
        adapterController.reload();
    }

    @Override
    public void init() {
        super.init();
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
            detailsCompass.replace(Route.SUCCESS_STORES_DETAILS, bundle);
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
    public void destroyView() {
        eventBus.unregister(this);
        super.destroyView();
    }

    public boolean isFilterFavorites() {
        return onlyFavorites;
    }

    public static interface View extends Presenter.View {

        boolean isTablet();

        boolean isLandscape();

        void setDetailsContainerVisibility(boolean b);

        IRoboSpiceAdapter<SuccessStory> getAdapter();

        void finishLoading(List<SuccessStory> items);

        void startLoading();

        void showOnlyFavorites(boolean onlyFavorites);
    }
}


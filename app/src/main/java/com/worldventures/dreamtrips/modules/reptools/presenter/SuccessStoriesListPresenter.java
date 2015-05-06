package com.worldventures.dreamtrips.modules.reptools.presenter;

import android.os.Bundle;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.OnSuccessStoryCellClickEvent;
import com.worldventures.dreamtrips.core.utils.events.SuccessStoryItemSelectedEvent;
import com.worldventures.dreamtrips.core.utils.events.SuccessStoryLikedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.reptools.api.successstories.GetSuccessStoriesQuery;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoriesDetailsFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuccessStoriesListPresenter extends Presenter<SuccessStoriesListPresenter.View> {

    private boolean onlyFavorites = false;

    private RoboSpiceAdapterController<SuccessStory> adapterController = new RoboSpiceAdapterController<SuccessStory>() {
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
            if (spiceException != null) {
                handleError(spiceException);
            }
        }
    };

    public SuccessStoriesListPresenter(View view) {
        super(view);
    }

    @Override
    public void resume() {
        adapterController.setSpiceManager(dreamSpiceManager);
        adapterController.setAdapter(view.getAdapter());
        adapterController.reload();
    }

    @Override
    public void destroyView() {
        eventBus.unregister(this);
        super.destroyView();
    }

    public void reload() {
        adapterController.reload();
    }

    public void onEvent(OnSuccessStoryCellClickEvent event) {
        handleListItemClick(event.getModelObject(), event.getPosition());
        view.onStoryClicked();
    }

    public void onEvent(SuccessStoryLikedEvent event) {
        reload();
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

    public boolean isFilterFavorites() {
        return onlyFavorites;
    }

    private void handleListItemClick(SuccessStory successStory, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SuccessStoriesDetailsFragment.EXTRA_STORY, successStory);
        if (view.isTabletLandscape()) {
            fragmentCompass.setContainerId(R.id.detail_container);
            fragmentCompass.replace(Route.SUCCESS_STORES_DETAILS, bundle);
            eventBus.post(new SuccessStoryItemSelectedEvent(position));
        } else {
            activityRouter.openSuccessStoryDetails(successStory);
        }
    }

    private ArrayList<SuccessStory> performFiltering(ArrayList<SuccessStory> successStories) {
        ArrayList<SuccessStory> result = new ArrayList<>();
        if (isFilterFavorites()) {
            for (SuccessStory successStory : successStories) {
                if (successStory.isLiked()) {
                    result.add(successStory);
                }
            }
        } else {
            result.addAll(successStories);
        }

        Collections.sort(result, (lhs, rhs) -> lhs.getAuthor().compareTo(rhs.getAuthor()));
        Collections.sort(result, (lhs, rhs) -> lhs.getCategory().compareTo(rhs.getCategory()));

        return result;
    }

    public interface View extends Presenter.View {

        FilterableArrayListAdapter getAdapter();

        void finishLoading(List<SuccessStory> items);

        void startLoading();

        void onStoryClicked();
    }
}


package com.worldventures.dreamtrips.modules.reptools.presenter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.events.OnSuccessStoryCellClickEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.reptools.api.successstories.GetSuccessStoriesQuery;
import com.worldventures.dreamtrips.modules.reptools.event.StoryLikedEvent;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoryDetailsFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuccessStoryListPresenter extends Presenter<SuccessStoryListPresenter.View> {

    private boolean onlyFavorites = false;
    private int lastSelectedPosition = -1;

    private DreamSpiceAdapterController<SuccessStory> adapterController = new DreamSpiceAdapterController<SuccessStory>() {
        @Override
        public SpiceRequest<ArrayList<SuccessStory>> getReloadRequest() {
            return new GetSuccessStoriesQuery();
        }

        @Override
        protected void onRefresh(ArrayList<SuccessStory> successStories) {
            super.onRefresh(performFiltering(successStories));
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<SuccessStory> items, SpiceException spiceException) {
            if (adapterController != null) {
                view.finishLoading(items);
                if (spiceException != null) {
                    handleError(spiceException);
                }
            }
        }
    };

    @Override
    public void onResume() {
        if (view.getAdapter().getCount() == 0) {
            adapterController.setSpiceManager(dreamSpiceManager);
            adapterController.setAdapter(view.getAdapter());
            adapterController.reload();
        }
    }

    @Override
    public void dropView() {
        adapterController.setAdapter(null);
        super.dropView();
    }

    public void reload() {
        adapterController.reload();
    }

    public void onEvent(OnSuccessStoryCellClickEvent event) {
        handleListItemClick(event.getModelObject(), event.getPosition());
        view.onStoryClicked();
    }

    public void onEvent(StoryLikedEvent event) {
        eventBus.removeStickyEvent(event);
        //
        List<SuccessStory> stories = view.getAdapter().getItems();
        Queryable.from(stories).filter(story -> story.getUrl().equals(event.storyUrl)).forEachR(story -> {
            story.setLiked(event.isLiked);
            view.getAdapter().notifyItemChanged(stories.indexOf(story));
        });
    }

    public void openFirst(SuccessStory successStory) {
        if (lastSelectedPosition == -1) {
            handleListItemClick(successStory, 0);
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

    public boolean isFilterFavorites() {
        return onlyFavorites;
    }

    private void handleListItemClick(SuccessStory successStory, int position) {
        lastSelectedPosition = position;
        Bundle bundle = new Bundle();
        bundle.putParcelable(SuccessStoryDetailsFragment.EXTRA_STORY, successStory);
        view.openStory(bundle);
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

        void openStory(Bundle bundle);

        FragmentManager getSupportFragmentManager();
    }
}


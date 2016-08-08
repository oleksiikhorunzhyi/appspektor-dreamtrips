package com.worldventures.dreamtrips.modules.reptools.presenter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.utils.delegate.StoryLikedEventDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.reptools.api.successstories.GetSuccessStoriesQuery;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoryDetailsFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class SuccessStoryListPresenter extends Presenter<SuccessStoryListPresenter.View> {

    private boolean onlyFavorites = false;
    private int lastSelectedPosition = -1;

    @Inject StoryLikedEventDelegate storyLikedEventDelegate;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        storyLikedEventDelegate.getObservable()
                .compose(bindViewToMainComposer())
                .subscribe(this::onSuccessStoryLiked);
    }

    @Override
    public void onResume() {
        if (view.getAdapter().getCount() == 0) reload();
    }

    public void reload() {
        view.startLoading();
        doRequest(new GetSuccessStoriesQuery(), items -> {
            view.finishLoading();
            //
            view.getAdapter().clear();
            view.getAdapter().addItems(performFiltering(items));
            view.getAdapter().notifyDataSetChanged();
        });
    }

    @Override
    public void handleError(SpiceException error) {
        view.finishLoading();
        super.handleError(error);
    }

    public void onSuccessStoryCellClick(SuccessStory successStory, int position) {
        handleListItemClick(successStory, position);
        view.onStoryClicked();
    }

    private void onSuccessStoryLiked(SuccessStory successStory) {
        List<SuccessStory> stories = view.getAdapter().getItems();
        Queryable.from(stories).filter(story -> story.getUrl().equals(successStory.getUrl())).forEachR(story -> {
            story.setLiked(successStory.isLiked());
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

        void finishLoading();

        void startLoading();

        void onStoryClicked();

        void openStory(Bundle bundle);

        FragmentManager getSupportFragmentManager();
    }
}


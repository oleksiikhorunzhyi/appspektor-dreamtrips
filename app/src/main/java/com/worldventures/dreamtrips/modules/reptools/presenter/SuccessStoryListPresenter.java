package com.worldventures.dreamtrips.modules.reptools.presenter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.techery.spares.utils.delegate.StoryLikedEventDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.service.SuccessStoriesInteractor;
import com.worldventures.dreamtrips.modules.reptools.service.command.GetSuccessStoriesCommand;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoryDetailsFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class SuccessStoryListPresenter extends Presenter<SuccessStoryListPresenter.View> {

   private boolean onlyFavorites = false;
   private int lastSelectedPosition = -1;

   @Inject StoryLikedEventDelegate storyLikedEventDelegate;
   @Inject SuccessStoriesInteractor successStoriesInteractor;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      storyLikedEventDelegate.getObservable().compose(bindViewToMainComposer())
            .subscribe(view::updateItem);
   }

   @Override
   public void onResume() {
      if (view.getItemsCount() == 0) reload();
   }

   public void reload() {
      view.startLoading();
      successStoriesInteractor.getSuccessStoriesPipe()
            .createObservable(new GetSuccessStoriesCommand())
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetSuccessStoriesCommand>()
               .onSuccess(command -> {
                  view.finishLoading();
                  view.setItems(performFiltering(command.getResult()));
               })
               .onFail((command, throwable) -> {
                  view.finishLoading();
                  handleError(command, throwable);
               }));
   }

   @Override
   public void handleError(Object action, Throwable error) {
      super.handleError(action, error);
      view.finishLoading();
   }

   public void onSuccessStoryCellClick(SuccessStory successStory, int position) {
      handleListItemClick(successStory, position);
      view.onStoryClicked();
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

   private ArrayList<SuccessStory> performFiltering(List<SuccessStory> successStories) {
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

      void setItems(List<SuccessStory> items);

      void updateItem(SuccessStory item);

      int getItemsCount();

      void finishLoading();

      void startLoading();

      void onStoryClicked();

      void openStory(Bundle bundle);

      FragmentManager getSupportFragmentManager();
   }
}


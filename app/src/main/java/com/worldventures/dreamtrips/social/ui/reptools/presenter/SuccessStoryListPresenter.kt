package com.worldventures.dreamtrips.social.ui.reptools.presenter

import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory
import com.worldventures.dreamtrips.social.ui.reptools.service.SuccessStoriesInteractor
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.FilterSuccessStoriesShowAllAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.FilterSuccessStoriesShowFavoriteAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.SearchSuccessStoriesAction
import com.worldventures.dreamtrips.social.ui.reptools.service.command.GetSuccessStoriesCommand
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.SuccessStoryDetailsFragment
import com.worldventures.dreamtrips.social.util.event_delegate.StoryLikedEventDelegate
import io.techery.janet.ActionState
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

class SuccessStoryListPresenter : Presenter<SuccessStoryListPresenter.View>() {

   private var isFilterFavorites = false
   private var lastSelectedPosition = -1

   @field:Inject lateinit var storyLikedEventDelegate: StoryLikedEventDelegate
   @field:Inject lateinit var successStoriesInteractor: SuccessStoriesInteractor

   override fun takeView(view: View) {
      super.takeView(view)
      storyLikedEventDelegate.observable.compose(bindViewToMainComposer())
            .subscribe(view::updateItem)
   }

   override fun onResume() {
      super.onResume()
      if (view.itemsCount == 0) reload()
   }

   fun reload() {
      view.startLoading()
      successStoriesInteractor.successStoriesPipe
            .createObservable(GetSuccessStoriesCommand())
            .compose<ActionState<GetSuccessStoriesCommand>>(bindViewToMainComposer<ActionState<GetSuccessStoriesCommand>>())
            .subscribe(ActionStateSubscriber<GetSuccessStoriesCommand>()
                  .onSuccess {
                     view.finishLoading()
                     view.setItems(performFiltering(it.result))
                  }
                  .onFail(this::handleError))
   }

   override fun handleError(action: Any, error: Throwable) {
      super.handleError(action, error)
      view.finishLoading()
   }

   fun onShowFilterRequired() {
      view.showFilterDialog(isFilterFavorites)
   }

   fun onSuccessStoryCellClick(successStory: SuccessStory, position: Int) {
      handleListItemClick(successStory, position)
      view.onStoryClicked()
   }

   fun openFirst(successStory: SuccessStory) {
      if (lastSelectedPosition == -1) {
         handleListItemClick(successStory, 0)
      }
   }

   fun reloadWithFilter(filterFavorites: Boolean) {
      this.isFilterFavorites = filterFavorites
      if (filterFavorites) {
         analyticsInteractor.analyticsActionPipe().send(FilterSuccessStoriesShowFavoriteAction())
      } else {
         analyticsInteractor.analyticsActionPipe().send(FilterSuccessStoriesShowAllAction())
      }
      reload()
   }

   private fun handleListItemClick(successStory: SuccessStory, position: Int) {
      lastSelectedPosition = position
      val bundle = Bundle()
      bundle.putParcelable(SuccessStoryDetailsFragment.EXTRA_STORY, successStory)
      view.openStory(bundle)
   }

   private fun performFiltering(successStories: List<SuccessStory>): List<SuccessStory> {
      return successStories
            .filter { !isFilterFavorites || it.isLiked }
            .sortedWith(compareBy({ it.category }, { it.author }))
   }

   fun onSearchActivated() = analyticsInteractor.analyticsActionPipe().send(SearchSuccessStoriesAction())

   interface View : Presenter.View {

      val itemsCount: Int

      val supportFragmentManager: FragmentManager

      fun setItems(items: List<SuccessStory>)

      fun updateItem(item: SuccessStory)

      fun finishLoading()

      fun startLoading()

      fun onStoryClicked()

      fun openStory(bundle: Bundle)

      fun showFilterDialog(showFavorites: Boolean)
   }
}


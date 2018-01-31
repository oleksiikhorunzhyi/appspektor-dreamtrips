package com.worldventures.dreamtrips.social.ui.reptools.presenter

import android.os.Bundle
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.service.reptools.SuccessStoriesInteractor
import com.worldventures.dreamtrips.social.service.reptools.command.GetSuccessStoriesCommand
import com.worldventures.dreamtrips.social.service.reptools.command.ReadSuccessStoriesCommand
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.FilterSuccessStoriesShowAllAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.FilterSuccessStoriesShowFavoriteAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.SearchSuccessStoriesAction
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.SuccessStoryDetailsFragment
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

open class SuccessStoryListPresenter : Presenter<SuccessStoryListPresenter.View>() {

   private var isFilterFavorites = false
   private var lastSelectedPosition = -1

   @Inject lateinit var successStoriesInteractor: SuccessStoriesInteractor

   override fun onViewTaken() {
      super.onViewTaken()
      subscribeSuccessStories()
      reload()
   }

   fun subscribeSuccessStories() {
      successStoriesInteractor.successStoriesPipe
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe { view.setItems(it.filteredResult()) }
   }

   fun reload() {
      successStoriesInteractor.getSuccessStoriesPipe
            .createObservable(GetSuccessStoriesCommand())
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetSuccessStoriesCommand>()
                  .onStart { view.startLoading() }
                  .onFinish { view.finishLoading() }
                  .onFail(this::handleError))
   }

   fun filterByQuery(query: String) {
      successStoriesInteractor.successStoriesPipe.send(ReadSuccessStoriesCommand {
         it.title.contains(query, ignoreCase = true) && (!isFilterFavorites || it.isLiked)
      })
   }

   fun filterFavorites(filterFavorites: Boolean) {
      this.isFilterFavorites = filterFavorites
      trackFilterPressed(filterFavorites)
      successStoriesInteractor.successStoriesPipe.send(ReadSuccessStoriesCommand { !isFilterFavorites || it.isLiked })
   }

   private fun trackFilterPressed(filterFavorites: Boolean) {
      if (filterFavorites) {
         analyticsInteractor.analyticsActionPipe().send(FilterSuccessStoriesShowFavoriteAction())
      } else {
         analyticsInteractor.analyticsActionPipe().send(FilterSuccessStoriesShowAllAction())
      }
   }

   fun onShowFilterRequired() = view.showFilterDialog(isFilterFavorites)

   fun onSuccessStoryCellClick(successStory: SuccessStory, position: Int) = openStory(successStory, position)

   fun openFirst(successStory: SuccessStory) {
      if (lastSelectedPosition == -1) {
         openStory(successStory, 0)
      }
   }

   fun onSearchActivated() = analyticsInteractor.analyticsActionPipe().send(SearchSuccessStoriesAction())

   fun openStory(successStory: SuccessStory, position: Int) {
      lastSelectedPosition = position
      view.openStory(Bundle().apply { putParcelable(SuccessStoryDetailsFragment.EXTRA_STORY, successStory) })
   }

   interface View : Presenter.View {
      fun setItems(items: List<SuccessStory>?)

      fun finishLoading()

      fun startLoading()

      fun openStory(bundle: Bundle)

      fun showFilterDialog(showFavorites: Boolean)
   }
}


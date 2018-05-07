package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.worldventures.dreamtrips.core.rx.RxView
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.bucketlist.analytics.BucketItemAddedFromPopularAnalyticsAction
import com.worldventures.dreamtrips.social.ui.bucketlist.analytics.BucketPopularTabViewAnalyticsAction
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.COMPLETED
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.NEW
import com.worldventures.dreamtrips.social.ui.bucketlist.model.PopularBucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.CreateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetPopularBucketItemSuggestionsCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetPopularBucketItemsCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.RecentlyAddedBucketsFromPopularCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketBodyImpl
import icepick.State
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

class BucketPopularPresenter(private val type: BucketItem.BucketType) : Presenter<BucketPopularPresenter.View>() {

   @Inject internal lateinit var bucketInteractor: BucketInteractor

   @JvmField @field:State var query: String = ""

   override fun onResume() {
      super.onResume()
      if (view.itemsCount == 0) {
         reload()
      }
   }

   fun onSearch(query: String) {
      this.query = query
      if (query.length > SEARCH_THRESHOLD) {
         searchPopularItems(query)
      }
   }

   internal fun searchPopularItems(query: String) =
      bucketInteractor.popularBucketItemSuggestionsPipe
            .createObservable(GetPopularBucketItemSuggestionsCommand(type, query))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetPopularBucketItemSuggestionsCommand>()
                  .onStart { view.startLoading() }
                  .onSuccess { onSearchSucceed(it.result) }
                  .onFail(this::handleError))

   fun onSelected() = analyticsInteractor.analyticsActionPipe().send(BucketPopularTabViewAnalyticsAction(type))

   private fun onSearchSucceed(items: List<PopularBucketItem>) {
      view.finishLoading()
      view.setFilteredItems(items)
   }

   fun searchClosed() = view.flushFilter()

   fun onAdd(popularBucketItem: PopularBucketItem) = add(popularBucketItem, false)

   fun onDone(popularBucketItem: PopularBucketItem) = add(popularBucketItem, true)

   internal fun add(popularBucketItem: PopularBucketItem, done: Boolean) =
      bucketInteractor.createPipe()
            .createObservable(CreateBucketItemCommand(ImmutableBucketBodyImpl.builder()
                  .type(type.getName())
                  .id(popularBucketItem.id.toString())
                  .status(if (done) COMPLETED else NEW)
                  .build()))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<CreateBucketItemCommand>()
                  .onSuccess { createBucketItemHttpAction ->
                     val bucketItem = createBucketItemHttpAction.result
                     analyticsInteractor.analyticsActionPipe()
                           .send(BucketItemAddedFromPopularAnalyticsAction(query))
                     bucketInteractor.recentlyAddedBucketsFromPopularCommandPipe()
                           .send(RecentlyAddedBucketsFromPopularCommand.add(bucketItem))
                     view.notifyItemWasAddedToBucketList(bucketItem)
                     view.removeItem(popularBucketItem)
                  }
                  .onFail { failedAction, throwable ->
                     handleError(failedAction, throwable)
                     popularBucketItem.isLoading = false
                     view.notifyItemsChanged()
                  })

   fun reload() =
      bucketInteractor.popularBucketItemsPipe
            .createObservable(GetPopularBucketItemsCommand(type))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetPopularBucketItemsCommand>()
                  .onStart { view.startLoading() }
                  .onSuccess {
                     view.finishLoading()
                     view.setItems(it.result)
                  }
                  .onFail(this::handleError))

   override fun handleError(action: Any, error: Throwable) {
      super.handleError(action, error)
      view.finishLoading()
   }

   interface View : RxView {
      val itemsCount: Int

      fun setItems(items: List<PopularBucketItem>)

      fun removeItem(item: PopularBucketItem)

      fun notifyItemsChanged()

      fun setFilteredItems(items: List<PopularBucketItem>)

      fun flushFilter()

      fun startLoading()

      fun finishLoading()

      fun notifyItemWasAddedToBucketList(bucketItem: BucketItem)
   }

   companion object {

      val SEARCH_THRESHOLD = 2
   }
}

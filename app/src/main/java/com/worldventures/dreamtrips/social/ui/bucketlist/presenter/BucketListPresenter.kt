package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.core.rx.RxView
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.bucketlist.analytics.BucketItemAddedAnalyticsAction
import com.worldventures.dreamtrips.social.ui.bucketlist.analytics.BucketTabViewAnalyticsAction
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketBundle
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.COMPLETED
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.NEW
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.CreateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.ApptentiveBucketListViewedAction
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.BucketItemAction
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.BucketListAction
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.BucketListCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketBodyImpl
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketPostBody
import com.worldventures.dreamtrips.social.ui.bucketlist.view.adapter.SuggestionLoader
import icepick.State
import io.techery.janet.Janet
import io.techery.janet.helper.ActionStateSubscriber
import java.util.ArrayList
import javax.inject.Inject

open class BucketListPresenter(@JvmField @field:State var type: BucketItem.BucketType) : Presenter<BucketListPresenter.View>() {

   @Inject internal lateinit var janetApi: Janet
   @Inject internal lateinit var bucketInteractor: BucketInteractor

   @JvmField @field:State internal var showToDo = true
   @JvmField @field:State internal var showCompleted = true
   @JvmField @field:State var lastOpenedBucketItem: BucketItem? = null
   internal var bucketItems: List<BucketItem> = ArrayList()
   internal var filteredItems: MutableList<BucketItem> = ArrayList()

   override fun takeView(view: View) {
      super.takeView(view)
      analyticsInteractor.analyticsActionPipe().send(ApptentiveBucketListViewedAction())
   }

   override fun onStart() {
      super.onStart()
      view.startLoading()
   }

   override fun onResume() {
      super.onResume()
      bucketInteractor.bucketListActionPipe()
            .observeWithReplay()
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe(ActionStateSubscriber<BucketListCommand>()
                  .onSuccess { onSuccessLoadingBucketList(it.result) }
                  .onFail { bucketListCommand, throwable ->
                     view.finishLoading()
                     handleError(bucketListCommand, throwable)
                  })
   }

   private fun onSuccessLoadingBucketList(newItems: List<BucketItem>) {
      bucketItems = newItems.filter { it.type.equals(type.name, ignoreCase = true) }
      view.finishLoading()
      refresh()
   }

   internal fun refresh() {
      filteredItems.clear()

      if (bucketItems.isEmpty()) {
         view.hideDetailContainer()
         view.setItems(filteredItems)
         return
      }

      if (showToDo) {
         val toDo = bucketItems
               .filter { !it.isDone }
               .toList()
         filteredItems.addAll(toDo)
      }
      view.putCategoryMarker(filteredItems.size)
      if (showCompleted) {
         val done = bucketItems.filter { it.isDone }.toList()
         filteredItems.addAll(done)
      }

      val lastOpenedItem = lastOpenedBucketItem
      val bucketToOpen = if (lastOpenedItem != null && filteredItems.contains(lastOpenedItem)) {
         lastOpenedBucketItem
      } else if (!filteredItems.isEmpty()) {
         filteredItems[0]
      } else null
      openDetailsIfNeeded(bucketToOpen)

      view.setItems(filteredItems)
      if (!filteredItems.isEmpty()) {
         view.hideEmptyView()
      }
   }

   fun itemClicked(bucketItem: BucketItem) {
      analyticsInteractor.analyticsActionPipe().send(BucketItemAction.view(bucketItem.uid))
      openDetails(bucketItem)
   }

   fun itemDoneClicked(bucketItem: BucketItem) {
      analyticsInteractor.analyticsActionPipe().send(BucketItemAction.complete(bucketItem.uid))
      markAsDone(bucketItem)
   }

   private fun openDetailsIfNeeded(item: BucketItem?) {
      if (!view.isTabletLandscape) return

      if (item != null) {
         openDetails(item)
      } else {
         view.hideDetailContainer()
      }
   }

   private fun openDetails(bucketItem: BucketItem) {
      lastOpenedBucketItem = bucketItem

      bucketItems.forEach { it.isSelected = it == bucketItem }

      view.openDetails(bucketItem)
      view.notifyItemsChanged()
   }

   fun popularClicked() {
      val bundle = BucketBundle()
      bundle.type = type
      view.openPopular(bundle)
      analyticsInteractor.analyticsActionPipe().send(BucketListAction.addFromPopular(type))
   }

   fun reloadWithFilter(filterId: Int) {
      when (filterId) {
         R.id.action_show_all -> {
            showToDo = true
            showCompleted = true
         }
         R.id.action_show_to_do -> {
            showToDo = true
            showCompleted = false
         }
         R.id.action_show_completed -> {
            showToDo = false
            showCompleted = true
         }
         else -> {
         }
      }
      refresh()
   }

   private fun markAsDone(bucketItem: BucketItem) =
      bucketInteractor.updatePipe()
            .createObservable(UpdateBucketItemCommand(ImmutableBucketBodyImpl.builder().id(bucketItem.uid)
                  .status(getMarkAsDoneStatus(bucketItem)).build()))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<UpdateBucketItemCommand>()
                  .onFail { markItemAsDoneAction, throwable ->
                     refresh()
                     handleError(markItemAsDoneAction, throwable)
                  })

   fun itemMoved(fromPosition: Int, toPosition: Int) {
      if (fromPosition != toPosition) {
         bucketInteractor.bucketListActionPipe()
               .send(BucketListCommand.move(getOriginalPosition(fromPosition), getOriginalPosition(toPosition), type))
      }
   }

   fun addToBucketList(title: String) =
         bucketInteractor.createPipe()
            .createObservable(CreateBucketItemCommand(ImmutableBucketPostBody
                  .builder()
                  .type(type.getName())
                  .name(title)
                  .status(NEW)
                  .build()))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<CreateBucketItemCommand>()
                  .onSuccess {
                     analyticsInteractor.analyticsActionPipe()
                           .send(BucketItemAddedAnalyticsAction(title))
                  }
                  .onFail(this::handleError))

   fun suggestionLoader() = SuggestionLoader(type, janetApi)

   private fun getOriginalPosition(filteredPosition: Int) = bucketItems.indexOf(filteredItems[filteredPosition])

   private fun getMarkAsDoneStatus(item: BucketItem) = if (item.isDone) NEW else COMPLETED

   fun onSelected() = analyticsInteractor.analyticsActionPipe().send(BucketTabViewAnalyticsAction(type))

   fun onFilterShown() = analyticsInteractor.analyticsActionPipe().send(BucketListAction.filter(type))

   interface View : RxView {

      fun setItems(items: List<BucketItem>)

      fun notifyItemsChanged()

      fun startLoading()

      fun finishLoading()

      fun showDetailsContainer()

      fun hideDetailContainer()

      fun putCategoryMarker(position: Int)

      fun hideEmptyView()

      fun openDetails(bucketItem: BucketItem)

      fun openPopular(args: BucketBundle)
   }
}

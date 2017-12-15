package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.core.rx.RxView
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.BucketType
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.BucketType.ACTIVITY
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.BucketType.DINING
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.BucketType.LOCATION
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.service.CurrentOpenTabEventDelegate
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.AdobeBucketListViewedAction
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.BucketListCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.RecentlyAddedBucketsFromPopularCommand
import io.techery.janet.helper.ActionStateSubscriber
import rx.Observable
import java.util.Arrays
import javax.inject.Inject

open class BucketTabsPresenter : Presenter<BucketTabsPresenter.View>() {
   @Inject internal lateinit var bucketInteractor: BucketInteractor
   @Inject internal lateinit var currentOpenTabEventDelegate: CurrentOpenTabEventDelegate

   protected open val user: User
      get() = account

   override fun onViewTaken() {
      super.onViewTaken()
      setTabs()
      loadBucketList()
      subscribeToErrorUpdates()
   }

   override fun onResume() {
      super.onResume()
      recentTabCountObservable().map { it.result }
            .compose(bindViewToMainComposer())
            .subscribe { view.setRecentBucketItemCountByType(it.first, it.second.size) }
   }

   override fun dropView() {
      super.dropView()
      currentOpenTabEventDelegate.post(null)
   }

   /**
    * We show single common connection overlay over the tabs content.
    * Subscribe to offline errors to be able to handle those happened in tabs and show it.
    */
   internal fun subscribeToErrorUpdates() =
      offlineErrorInteractor.offlineErrorCommandPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe { reportNoConnection() }

   internal fun loadBucketList() =
      bucketInteractor.bucketListActionPipe()
            .createObservable(BucketListCommand.fetch(user, false))
            .compose(bindViewToMainComposer())
            .concatMap { state ->
               if (state.action.isFromCache)
                  bucketInteractor.bucketListActionPipe()
                        .createObservable(BucketListCommand.fetch(user, true))
               else
                  Observable.just(state)
            }
            .subscribe(ActionStateSubscriber<BucketListCommand>()
                  .onFail(this::handleError))

   internal fun setTabs() {
      view.setTypes(Arrays.asList(LOCATION, ACTIVITY, DINING))
      view.updateSelection()
   }

   fun onTabChange(type: BucketType) {
      bucketInteractor.recentlyAddedBucketsFromPopularCommandPipe()
            .send(RecentlyAddedBucketsFromPopularCommand.clear(type))
      currentOpenTabEventDelegate.post(type)
   }

   private fun recentTabCountObservable(): Observable<RecentlyAddedBucketsFromPopularCommand> {
      val recentPipe = bucketInteractor.recentlyAddedBucketsFromPopularCommandPipe()
      return Observable.merge(recentPipe.createObservableResult(RecentlyAddedBucketsFromPopularCommand.get(LOCATION)), recentPipe
            .createObservableResult(RecentlyAddedBucketsFromPopularCommand.get(ACTIVITY)), recentPipe.createObservableResult(RecentlyAddedBucketsFromPopularCommand
            .get(DINING)))
   }

   fun onTrackListOpened() = analyticsInteractor.analyticsActionPipe().send(AdobeBucketListViewedAction())

   interface View : RxView {
      fun setTypes(type: List<BucketType>)

      fun setRecentBucketItemCountByType(type: BucketType, count: Int)

      fun updateSelection()
   }
}

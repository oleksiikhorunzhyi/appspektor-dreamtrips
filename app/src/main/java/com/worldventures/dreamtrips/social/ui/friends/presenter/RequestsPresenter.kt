package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.core.ui.view.adapter.BaseArrayListAdapter
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesDecoratorCommand
import com.worldventures.dreamtrips.social.service.users.request.command.AcceptAllFriendRequestsCommand
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.request.command.DeleteFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.request.command.GetRequestsCommand
import com.worldventures.dreamtrips.social.service.users.request.command.SortRequestsStorageCommand
import com.worldventures.dreamtrips.social.service.users.request.delegate.RequestsStorageDelegate
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.FriendsAnalyticsAction
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle
import com.worldventures.dreamtrips.social.ui.profile.service.analytics.FriendRelationshipAnalyticAction
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

open class RequestsPresenter : Presenter<RequestsPresenter.View>() {

   @Inject lateinit var snappyRepository: SnappyRepository
   @Inject lateinit var friendStorageDelegate: RequestsStorageDelegate

   override fun takeView(view: View?) {
      super.takeView(view)
      observeOnUpdateRequestsStorage()
      observeOnError()
      reloadRequests()
   }

   private fun observeOnUpdateRequestsStorage() {
      friendStorageDelegate.observeOnGetCirclesCommand()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetCirclesDecoratorCommand>()
                  .onFail(this::handleError))

      friendStorageDelegate.observOnSortRequets(context.getString(R.string.request_incoming_long),
            context.getString(R.string.request_outgoing_long))
            .compose(bindViewToMainComposer())
            .subscribe(this::successUpdateStorage)
   }

   private fun observeOnError() {
      friendStorageDelegate.friendInteractor.requestsPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetRequestsCommand>()
                  .onFail(this::handleError))

      friendStorageDelegate.friendInteractor.acceptAllPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<AcceptAllFriendRequestsCommand>()
                  .onFail(this::handleError))

      friendStorageDelegate.friendInteractor.acceptRequestPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<ActOnFriendRequestCommand.Accept>()
                  .onFail(this::handleError))

      friendStorageDelegate.friendInteractor.rejectRequestPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<ActOnFriendRequestCommand.Reject>()
                  .onFail(this::handleError))

      friendStorageDelegate.friendInteractor.deleteRequestPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<DeleteFriendRequestCommand>()
                  .onFail(this::handleError))
   }

   private fun successUpdateStorage(resultCommand: SortRequestsStorageCommand) {
      view.finishLoading()
      view.itemsLoaded(resultCommand.result, resultCommand.isNoMoreItems)
   }

   override fun handleError(action: Any?, error: Throwable?) {
      view.finishLoading()
      view.notifyItemsStateChanged()
      super.handleError(action, error)
   }

   open fun reloadRequests() {
      loadRequests(true)
   }

   fun loadNext() {
      loadRequests(false)
   }

   private fun loadRequests(reload: Boolean = false) {
      view.startLoading()
      friendStorageDelegate.loadRequests(reload) { GetRequestsCommand(it) }
   }

   fun userClicked(user: User) {
      view.openUser(UserBundle(user))
   }

   fun rejectRequest(user: User) {
      view.startLoading()
      analyticsInteractor.analyticsActionPipe().send(FriendRelationshipAnalyticAction.rejectRequest())
      friendStorageDelegate.rejectRequest(user)
   }

   open fun hideRequest(user: User) {
      friendStorageDelegate.deleteRequest(user, DeleteFriendRequestCommand.Action.HIDE)
   }

   open fun cancelRequest(user: User) {
      view.startLoading()
      analyticsInteractor.analyticsActionPipe().send(FriendRelationshipAnalyticAction.cancelRequest())
      friendStorageDelegate.deleteRequest(user, DeleteFriendRequestCommand.Action.CANCEL)
   }

   fun acceptAllRequests() {
      friendStorageDelegate.getCircles { circles ->
         view.showAddFriendDialog(circles) { selectedCircle ->
            view.startLoading()
            friendStorageDelegate.acceptAllRequests(selectedCircle)
         }
      }
   }

   fun acceptRequest(user: User) {
      friendStorageDelegate.getCircles { circles ->
         view.showAddFriendDialog(circles) { selectedCircle ->
            view.startLoading()
            friendStorageDelegate.acceptRequest(user, selectedCircle)
         }
      }
   }

   fun onAddFriendsPressed() {
      analyticsInteractor.analyticsActionPipe().send(FriendsAnalyticsAction.addFriends())
      analyticsInteractor.analyticsActionPipe().send(FriendsAnalyticsAction.searchFriends())
   }

   interface View : Presenter.View, BlockingProgressView {
      fun startLoading()

      fun openUser(openUser: UserBundle)

      fun finishLoading()

      fun itemsLoaded(sortedItems: List<Any>, noMoreElements: Boolean)

      fun notifyItemsStateChanged()

      fun showAddFriendDialog(circles: List<Circle>, selectAction: (Circle) -> Unit)

      fun getAdapter(): BaseArrayListAdapter<Any>
   }
}

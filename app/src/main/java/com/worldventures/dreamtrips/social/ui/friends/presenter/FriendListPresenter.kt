package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.social.service.users.base.analytics.FilterFriendsFeedAction
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesDecoratorCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.GetFriendsCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.RemoveFriendCommand
import com.worldventures.dreamtrips.social.service.users.friend.delegate.FriendsListStorageDelegate
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.FriendsAnalyticsAction
import icepick.State
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

open class FriendListPresenter : BaseUserListPresenter<FriendListPresenter.View>() {

   @JvmField
   @State
   var selectedCircle: Circle? = null

   @JvmField
   @State
   var position = 0

   @JvmField
   @State
   var query: String = ""

   @Inject lateinit var friendsStorageDelegate: FriendsListStorageDelegate

   override fun subscribeOnStorage() {
      friendsStorageDelegate.observeOnGetCirclesCommand()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetCirclesDecoratorCommand>()
                  .onFail(this::onCirclesError))

      friendsStorageDelegate.observeOnUpdateStorage()
            .map {
               canLoadMore = !it.isNoMoreItems()
               it.getStorageItems()
            }.compose(bindViewToMainComposer())
            .subscribe(this::finishUpdateStorage)
   }

   override fun subscribeOnErrors() {
      friendsStorageDelegate.friendInteractor.friendsPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetFriendsCommand>()
                  .onFail(this::handleError))

      friendsStorageDelegate.friendInteractor.removeFriendPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<RemoveFriendCommand>()
                  .onFail(this::handleError))
   }

   private fun isNeedSearch(currentQuery: String, previousQuery: String): Boolean {
      return !(currentQuery.length < MIN_SEARCH_QUERY_LENGTH
            && (previousQuery.length < currentQuery.length || previousQuery.length < MIN_SEARCH_QUERY_LENGTH))
   }

   fun onFilterClicked() {
      friendsStorageDelegate.getCircles(this::onCirclesFilterSuccess)
   }

   fun reloadWithFilter(circle: Circle, position: Int) {
      selectedCircle = circle
      this.position = position
      reload()
      analyticsInteractor.analyticsActionPipe().send(FilterFriendsFeedAction(circle.name))
   }

   fun search(query: String) {
      val previousQuery = this.query
      this.query = query
      if (isNeedSearch(query, previousQuery)) reload()
   }

   fun onAddFriendsPressed() {
      analyticsInteractor.analyticsActionPipe().send(FriendsAnalyticsAction.addFriends())
      analyticsInteractor.analyticsActionPipe().send(FriendsAnalyticsAction.searchFriends())
   }

   fun unfriend(user: User) {
      friendsStorageDelegate.removeFriend(RemoveFriendCommand(user))
   }

   private fun onCirclesFilterSuccess(circles: List<Circle>) {
      val filters = circles.toMutableList()
      filters.add(0, Circle.withTitle(context.getString(R.string.show_all)))
      view.showFilters(filters, position)
   }

   override fun loadUsers(reload: Boolean) {
      friendsStorageDelegate.loadFriends(reload) { page, perPage ->
         GetFriendsCommand(selectedCircle, query, page, perPage)
      }
   }

   interface View : BaseUserListPresenter.View {
      fun showFilters(circle: List<Circle>, selectedPosition: Int)
   }

   companion object {
      const val MIN_SEARCH_QUERY_LENGTH = 3
   }
}

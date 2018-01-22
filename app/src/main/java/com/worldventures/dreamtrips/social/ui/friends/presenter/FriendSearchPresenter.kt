package com.worldventures.dreamtrips.social.ui.friends.presenter

import android.support.annotation.StringRes
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserStorageCommand
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesDecoratorCommand
import com.worldventures.dreamtrips.social.service.users.search.command.AddFriendCommand
import com.worldventures.dreamtrips.social.service.users.search.command.GetSearchUsersCommand
import com.worldventures.dreamtrips.social.service.users.search.delegate.SearchedUsersStorageDelegate
import icepick.State
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

open class FriendSearchPresenter(@JvmField @State var query: String)
   : BaseUserListPresenter<FriendSearchPresenter.View>() {

   @Inject lateinit var friendsStorageDelegate: SearchedUsersStorageDelegate
   private var usersCount = 0

   override fun subscribeOnStorage() {
      friendsStorageDelegate.observeOnGetCirclesCommand()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetCirclesDecoratorCommand>()
                  .onFail(this::onCirclesError))

      friendsStorageDelegate.observeOnUpdateStorage()
            .compose(bindViewToMainComposer())
            .subscribe(this::finishUpdateStorage)
   }

   override fun finishUpdateStorage(resultCommand: BaseUserStorageCommand) {
      super.finishUpdateStorage(resultCommand)
      usersCount = resultCommand.getStorageItems().size
      updateEmptyView(false)
      if (usersCount == 0 && query.length < MIN_SEARCH_QUERY_LENGTH) {
         view.updateEmptyCaption(R.string.start_searching)
      }
   }

   override fun subscribeOnErrors() {
      friendsStorageDelegate.friendInteractor.searchUsersPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetSearchUsersCommand>()
                  .onFail(this::handleError))

      friendsStorageDelegate.friendInteractor.addFriendPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<AddFriendCommand>()
                  .onFail(this::handleError))
   }

   override fun handleError(action: Any?, error: Throwable?) {
      super.handleError(action, error)
      updateEmptyView(false)
   }

   private fun updateEmptyView(isLoading: Boolean) {
      view.updateEmptyView(usersCount, isLoading)
   }

   override fun reload() {
      super.reload()
      updateEmptyView(true)
   }

   override fun loadUsers(reload: Boolean) {
      friendsStorageDelegate.searchUsers(query, reload)
   }

   fun search(query: String) {
      this.query = query
      reload()
   }

   fun addUserRequest(user: User) {
      friendsStorageDelegate.getCircles { circles ->
         view.showAddFriendDialog(circles) { selectedCircle ->
            view.startLoading()
            friendsStorageDelegate.addFriend(user, selectedCircle)
         }
      }
   }

   override fun isNeedPreload() = false

   interface View : BaseUserListPresenter.View {
      fun updateEmptyView(friendsSize: Int, isLoading: Boolean)
      fun updateEmptyCaption(@StringRes resource: Int)
   }

   companion object {
      const val MIN_SEARCH_QUERY_LENGTH = 3
   }
}

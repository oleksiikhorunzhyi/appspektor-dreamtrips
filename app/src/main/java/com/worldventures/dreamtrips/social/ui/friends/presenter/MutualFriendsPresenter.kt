package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.users.friend.command.GetMutualFriendsCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.RemoveFriendCommand
import com.worldventures.dreamtrips.social.service.users.friend.delegate.MutualFriendsStorageDelegate
import com.worldventures.dreamtrips.social.ui.friends.bundle.MutualFriendsBundle
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

open class MutualFriendsPresenter(bundle: MutualFriendsBundle) : BaseUserListPresenter<MutualFriendsPresenter.View>() {

   @Inject lateinit var friendsStorageDelegate: MutualFriendsStorageDelegate
   private val userId: Int = bundle.id

   override fun subscribeOnStorage() {
      friendsStorageDelegate.observeOnUpdateStorage(userId.toString())
            .compose(bindViewToMainComposer())
            .subscribe(this::finishUpdateStorage)
   }

   override fun subscribeOnErrors() {
      friendsStorageDelegate.friendInteractor.mutualFriendsPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetMutualFriendsCommand>()
                  .onFail(this::handleError))

      friendsStorageDelegate.friendInteractor.removeFriendPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<RemoveFriendCommand>()
                  .onFail(this::handleError))
   }

   override fun loadUsers(reload: Boolean) {
      friendsStorageDelegate.loadMutualFriends(reload) { page, perPage ->
         GetMutualFriendsCommand(userId, page, perPage)
      }
   }

   fun unfriend(user: User) {
      friendsStorageDelegate.removeFriend(RemoveFriendCommand(user))
   }

   interface View : BaseUserListPresenter.View
}

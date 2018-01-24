package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesDecoratorCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.RemoveFriendCommand
import com.worldventures.dreamtrips.social.service.users.liker.command.GetLikersCommand
import com.worldventures.dreamtrips.social.service.users.liker.delegate.LikersStorageDelegate
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.search.command.AddFriendCommand
import com.worldventures.dreamtrips.social.ui.friends.bundle.UsersLikedEntityBundle
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

open class UsersLikedItemPresenter(bundle: UsersLikedEntityBundle) :
      BaseUserListPresenter<BaseUserListPresenter.View>() {

   @Inject lateinit var friendsStorageDelegate: LikersStorageDelegate
   private val feedEntity = bundle.feedEntity

   override fun subscribeOnStorage() {
      friendsStorageDelegate.observeOnGetCirclesCommand()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetCirclesDecoratorCommand>()
                  .onFail(this::onCirclesError))

      friendsStorageDelegate.observeOnUpdateStorage()
            .compose(bindViewToMainComposer())
            .subscribe(this::finishUpdateStorage)
   }

   override fun subscribeOnErrors() {
      friendsStorageDelegate.friendInteractor.likersPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetLikersCommand>()
                  .onFail(this::handleError))

      friendsStorageDelegate.friendInteractor.addFriendPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<AddFriendCommand>()
                  .onFail(this::handleError))

      friendsStorageDelegate.friendInteractor.removeFriendPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<RemoveFriendCommand>()
                  .onFail(this::handleError))

      friendsStorageDelegate.friendInteractor.acceptRequestPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<ActOnFriendRequestCommand.Accept>()
                  .onFail(this::handleError))
   }

   override fun loadUsers(reload: Boolean) {
      friendsStorageDelegate.loadLikers(reload) { page, perPage ->
         GetLikersCommand(feedEntity, page, perPage)
      }
   }

   fun acceptRequest(user: User) {
      friendsStorageDelegate.getCircles { circles ->
         view.showAddFriendDialog(circles) { selectedCircle ->
            view.startLoading()
            friendsStorageDelegate.acceptRequest(user, selectedCircle)
         }
      }
   }

   fun unfriend(user: User) {
      view.startLoading()
      friendsStorageDelegate.removeFriend(RemoveFriendCommand(user))
   }

   open fun addUserRequest(user: User) {
      friendsStorageDelegate.getCircles { circles ->
         view.showAddFriendDialog(circles) { selectedCircle ->
            view.startLoading()
            friendsStorageDelegate.addFriend(user, selectedCircle)
         }
      }
   }
}

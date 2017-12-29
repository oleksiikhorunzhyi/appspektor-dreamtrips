package com.worldventures.dreamtrips.social.service.friends.storage.delegat

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation
import com.worldventures.dreamtrips.social.service.friends.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.friends.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetLikersCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetMutualFriendsCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetRequestsCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetSearchUsersCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.UserPaginationCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.ChangeCirclesCommand
import com.worldventures.dreamtrips.social.service.friends.storage.command.UserStorageCommand
import com.worldventures.dreamtrips.social.service.friends.storage.operation.AcceptAllOperation
import com.worldventures.dreamtrips.social.service.friends.storage.operation.AddFriendOperation
import com.worldventures.dreamtrips.social.service.friends.storage.operation.ChangeCircleOperation
import com.worldventures.dreamtrips.social.service.friends.storage.operation.EmptyListOperation
import com.worldventures.dreamtrips.social.service.friends.storage.operation.RemoveFriendOperation
import com.worldventures.dreamtrips.social.service.friends.storage.operation.RemoveRequestOperation
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetFriendsCommand
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor
import rx.Observable
import javax.inject.Inject

class FriendStorageDelegate @Inject constructor(val friendInteractor: FriendsInteractor
                                                , val circlesInteractor: CirclesInteractor
                                                , val profileInteractor: ProfileInteractor) {

   private fun observeCommands(): Observable<FriendListStorageOperation> {
      return Observable.merge(
            friendInteractor.userPaginationPipe.observeSuccess().map { EmptyListOperation(it.result, it.isFirstPage) },
            friendInteractor.addFriendPipe.observeSuccess().map { AddFriendOperation(it.result) },
            friendInteractor.acceptAllPipe.observeSuccess().map { AcceptAllOperation() },
            friendInteractor.acceptRequestPipe.observeSuccess().map { RemoveRequestOperation(it.result) },
            friendInteractor.deleteRequestPipe.observeSuccess().map { RemoveRequestOperation(it.result) },
            friendInteractor.rejectRequestPipe.observeSuccess().map { RemoveRequestOperation(it.result) },
            friendInteractor.removeFriendPipe.observeSuccess().map { RemoveFriendOperation(it.result) },
            profileInteractor.addFriendToCirclesPipe().observeSuccess().map { ChangeCircleOperation(it.userId, { circles -> circles.add(it.circle) }) },
            profileInteractor.removeFriendFromCirclesPipe().observeSuccess().map { ChangeCircleOperation(it.userId, { circles -> circles.remove(it.circle) }) }
      )
   }

   fun observeOnUpdateStorage() = observeCommands().flatMap {
      friendInteractor.storageCommand.createObservable(UserStorageCommand(it))
   }

   fun observeOnChangeCircleCommand() = friendInteractor.changeCirclePipe.observeWithReplay()

   fun loadFriends(createCommandAction: (page: Int, perPage: Int) -> GetFriendsCommand
                   , isLoadNext: Boolean = false) {
      with(friendInteractor) {
         userPaginationPipe.send(UserPaginationCommand(!isLoadNext, { page, perPage ->
            friendsPipe.createObservableResult(createCommandAction.invoke(page, perPage))
         }))
      }
   }

   fun loadRequests(createCommandAction: (page: Int) -> GetRequestsCommand
                    , isLoadNext: Boolean = false) {
      with(friendInteractor) {
         userPaginationPipe.send(UserPaginationCommand(!isLoadNext, { page, _ ->
            requestsPipe.createObservableResult(createCommandAction.invoke(page))
         }))
      }
   }

   fun loadLikers(createCommandAction: (page: Int, perPage: Int) -> GetLikersCommand
                  , isLoadNext: Boolean = false) {
      with(friendInteractor) {
         userPaginationPipe.send(UserPaginationCommand(!isLoadNext, { page, perPage ->
            likersPipe.createObservableResult(createCommandAction.invoke(page, perPage))
         }))
      }
   }

   fun loadMutualFriends(createCommandAction: (Int, Int) -> GetMutualFriendsCommand, isLoadNext: Boolean = false) {
      with(friendInteractor) {
         userPaginationPipe.send(UserPaginationCommand(!isLoadNext, { page, perPage ->
            mutualFriendsPipe.createObservableResult(createCommandAction.invoke(page, perPage))
         }))
      }
   }

   fun searchUsers(createCommandAction: (page: Int, perPage: Int) -> GetSearchUsersCommand
                   , isLoadNext: Boolean = false) {
      with(friendInteractor) {
         userPaginationPipe.send(UserPaginationCommand(!isLoadNext, { page, perPage ->
            searchUsersPipe.createObservableResult(createCommandAction.invoke(page, perPage))
         }))
      }
   }

   fun changeCircle(user: User, changeCircleAction: (user: User, circles: List<Circle>) -> Unit) {
      friendInteractor.changeCirclePipe.send(ChangeCirclesCommand(user, circlesInteractor.pipe, changeCircleAction))
   }

   abstract class FriendListStorageOperation(val refresh: Boolean) : ListStorageOperation<User>

}

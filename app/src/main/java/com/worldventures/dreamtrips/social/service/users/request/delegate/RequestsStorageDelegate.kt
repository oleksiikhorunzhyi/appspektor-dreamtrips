package com.worldventures.dreamtrips.social.service.users.request.delegate

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.users.base.delegate.BaseUserStorageDelegate
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsStorageInteractor
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation
import com.worldventures.dreamtrips.social.service.users.base.operation.EmptyUserStorageOperation
import com.worldventures.dreamtrips.social.service.users.request.command.AcceptAllFriendRequestsCommand
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.request.command.DeleteFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.request.command.GetRequestsCommand
import com.worldventures.dreamtrips.social.service.users.request.command.GetRequestsPaginationCommand
import com.worldventures.dreamtrips.social.service.users.request.command.SortRequestsStorageCommand
import com.worldventures.dreamtrips.social.service.users.request.command.UserRequestsStorageCommand
import com.worldventures.dreamtrips.social.service.users.request.operation.AddFriendOperation
import com.worldventures.dreamtrips.social.service.users.search.command.AddFriendCommand
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor
import com.worldventures.dreamtrips.social.ui.profile.service.command.AddFriendToCircleCommand
import com.worldventures.dreamtrips.social.ui.profile.service.command.RemoveFriendFromCircleCommand

class RequestsStorageDelegate(
      friendInteractor: FriendsInteractor,
      friendsStorageInteractor: FriendsStorageInteractor,
      circlesInteractor: CirclesInteractor,
      profileInteractor: ProfileInteractor
) : BaseUserStorageDelegate<GetRequestsPaginationCommand, UserRequestsStorageCommand>(
      friendInteractor,
      friendsStorageInteractor,
      circlesInteractor,
      profileInteractor
) {

   fun observOnSortRequets(incomingTitle: String, outgoingTitle: String) = observeOnUpdateStorage().flatMap {
      friendsStorageInteractor.sortRequestsPipe.createObservableResult(SortRequestsStorageCommand(it.acceptedCount, it.result,
            it.isNoMoreItems(), incomingTitle, outgoingTitle)
      )
   }

   fun loadRequests(reload: Boolean = false, createCommandAction: (page: Int) -> GetRequestsCommand) {
      getPaginationPipe().send(GetRequestsPaginationCommand(reload) { page, _ ->
         friendInteractor.requestsPipe.createObservableResult(createCommandAction.invoke(page))
      })
   }

   fun acceptRequest(user: User, circle: Circle) {
      user.circles.add(circle)
      friendInteractor.acceptRequestPipe.send(ActOnFriendRequestCommand.Accept(user, circle.id))
   }

   fun rejectRequest(user: User) {
      friendInteractor.rejectRequestPipe.send(ActOnFriendRequestCommand.Reject(user))
   }

   fun acceptAllRequests(circle: Circle) {
      friendInteractor.acceptAllPipe.send(AcceptAllFriendRequestsCommand(circle.id))
   }

   fun deleteRequest(user: User, action: DeleteFriendRequestCommand.Action) {
      friendInteractor.deleteRequestPipe.send(DeleteFriendRequestCommand(user, action))
   }

   override fun createStorageCommand(storageOperation: BaseUserStorageOperation) = UserRequestsStorageCommand(storageOperation)

   override fun getStoragePipe() = friendsStorageInteractor.userRequestsStoragePipe

   override fun getPaginationPipe() = friendsStorageInteractor.userRequestsPaginationPipe

   override fun getAddFriendOperation(resultCommand: AddFriendCommand) = AddFriendOperation(resultCommand.user)

   override fun getAddToCircleOperation(resultCommand: AddFriendToCircleCommand) = EmptyUserStorageOperation()

   override fun getRemoveFromCircleOperation(resultCommand: RemoveFriendFromCircleCommand) = EmptyUserStorageOperation()

}

package com.worldventures.dreamtrips.social.service.users.friend.delegate

import com.worldventures.dreamtrips.social.service.users.base.delegate.BaseUserStorageDelegate
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsStorageInteractor
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation
import com.worldventures.dreamtrips.social.service.users.friend.command.FriendListStorageCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.GetFriendsCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.GetFriendsPaginationCommand
import com.worldventures.dreamtrips.social.service.users.friend.operation.AcceptAllOperation
import com.worldventures.dreamtrips.social.service.users.friend.operation.AcceptFriendOperation
import com.worldventures.dreamtrips.social.service.users.request.command.AcceptAllFriendRequestsCommand
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor

class FriendsListStorageDelegate(
      friendInteractor: FriendsInteractor,
      friendsStorageInteractor: FriendsStorageInteractor,
      circlesInteractor: CirclesInteractor,
      profileInteractor: ProfileInteractor
) : BaseUserStorageDelegate<GetFriendsPaginationCommand, FriendListStorageCommand>(
      friendInteractor,
      friendsStorageInteractor,
      circlesInteractor,
      profileInteractor
) {

   fun loadFriends(reload: Boolean = true, createCommandAction: (page: Int, perPage: Int) -> GetFriendsCommand) {
      getPaginationPipe().send(GetFriendsPaginationCommand(reload) { page, perPage ->
         friendInteractor.friendsPipe.createObservableResult(createCommandAction.invoke(page, perPage))
      })
   }

   override fun createStorageCommand(storageOperation: BaseUserStorageOperation) = FriendListStorageCommand(storageOperation)

   override fun getStoragePipe() = friendsStorageInteractor.friendsListStoragePipe

   override fun getPaginationPipe() = friendsStorageInteractor.friendsListPaginationPipe

   override fun getAcceptRequestOperation(resultCommand: ActOnFriendRequestCommand.Accept) = AcceptFriendOperation(resultCommand.user)

   override fun getAcceptAllOperation(resultCommand: AcceptAllFriendRequestsCommand) = AcceptAllOperation({
      loadFriends(true, { page, perPage -> GetFriendsCommand("", page, perPage) })
   })

}

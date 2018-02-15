package com.worldventures.dreamtrips.social.service.users.liker.delegate

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.profile.ProfileInteractor
import com.worldventures.dreamtrips.social.service.users.base.delegate.BaseUserStorageDelegate
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsStorageInteractor
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation
import com.worldventures.dreamtrips.social.service.users.friend.command.RemoveFriendCommand
import com.worldventures.dreamtrips.social.service.users.liker.command.GetLikersCommand
import com.worldventures.dreamtrips.social.service.users.liker.command.GetLikersPaginationCommand
import com.worldventures.dreamtrips.social.service.users.liker.command.LikerStorageCommand
import com.worldventures.dreamtrips.social.service.users.liker.operation.AcceptRequestOperation
import com.worldventures.dreamtrips.social.service.users.liker.operation.RemoveFriendOperation
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand

class LikersStorageDelegate(
      friendInteractor: FriendsInteractor,
      friendsStorageInteractor: FriendsStorageInteractor,
      circlesInteractor: CirclesInteractor,
      profileInteractor: ProfileInteractor
) : BaseUserStorageDelegate<GetLikersPaginationCommand, LikerStorageCommand>(
      friendInteractor,
      friendsStorageInteractor,
      circlesInteractor,
      profileInteractor
) {

   fun loadLikers(reload: Boolean = true, createCommandAction: (page: Int, perPage: Int) -> GetLikersCommand) {
      getPaginationPipe().send(GetLikersPaginationCommand(reload) { page, perPage ->
         friendInteractor.likersPipe.createObservableResult(createCommandAction.invoke(page, perPage))
      })
   }

   fun acceptRequest(user: User, circle: Circle) {
      friendInteractor.acceptRequestPipe.send(ActOnFriendRequestCommand.Accept(user, circle.id))
   }

   override fun createStorageCommand(storageOperation: BaseUserStorageOperation) = LikerStorageCommand(storageOperation)

   override fun getStoragePipe() = friendsStorageInteractor.likersStoragePipe

   override fun getPaginationPipe() = friendsStorageInteractor.likersPaginationPipe

   override fun getRemoveFriendOperation(resultCommand: RemoveFriendCommand) = RemoveFriendOperation(resultCommand.user)

   override fun getAcceptRequestOperation(resultCommand: ActOnFriendRequestCommand.Accept) = AcceptRequestOperation(resultCommand.user)

}

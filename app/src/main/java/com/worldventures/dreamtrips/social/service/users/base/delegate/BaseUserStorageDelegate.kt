package com.worldventures.dreamtrips.social.service.users.base.delegate

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserPaginationCommand
import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserStorageCommand
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsStorageInteractor
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation
import com.worldventures.dreamtrips.social.service.users.base.operation.DefaultAcceptAllOperation
import com.worldventures.dreamtrips.social.service.users.base.operation.DefaultAcceptRequestOperation
import com.worldventures.dreamtrips.social.service.users.base.operation.DefaultAddFriendOperation
import com.worldventures.dreamtrips.social.service.users.base.operation.DefaultChangeCircleOperation
import com.worldventures.dreamtrips.social.service.users.base.operation.DefaultDeleteRequestOperation
import com.worldventures.dreamtrips.social.service.users.base.operation.DefaultRejectRequestOperation
import com.worldventures.dreamtrips.social.service.users.base.operation.DefaultRemoveFriendOperation
import com.worldventures.dreamtrips.social.service.users.base.operation.DefaultUpdateListOperation
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesDecoratorCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.RemoveFriendCommand
import com.worldventures.dreamtrips.social.service.users.request.command.AcceptAllFriendRequestsCommand
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.request.command.DeleteFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.search.command.AddFriendCommand
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor
import com.worldventures.dreamtrips.social.ui.profile.service.command.AddFriendToCircleCommand
import com.worldventures.dreamtrips.social.ui.profile.service.command.RemoveFriendFromCircleCommand
import io.techery.janet.ActionPipe
import rx.Observable

abstract class BaseUserStorageDelegate<PC : BaseUserPaginationCommand, SC : BaseUserStorageCommand>
(
      val friendInteractor: FriendsInteractor,
      val friendsStorageInteractor: FriendsStorageInteractor,
      val circlesInteractor: CirclesInteractor,
      val profileInteractor: ProfileInteractor
) {

   protected fun observeCommands(): Observable<BaseUserStorageOperation> {
      return Observable.merge(
            getPaginationPipe().observeSuccess().map(this::getUpdateListOperation),
            friendInteractor.addFriendPipe.observeSuccess().map(this::getAddFriendOperation),
            friendInteractor.acceptAllPipe.observeSuccess().map(this::getAcceptAllOperation),
            friendInteractor.acceptRequestPipe.observeSuccess().map(this::getAcceptRequestOperation),
            friendInteractor.deleteRequestPipe.observeSuccess().map(this::getDeleteRequestOperation),
            friendInteractor.rejectRequestPipe.observeSuccess().map(this::getRejectRequestOperation),
            friendInteractor.removeFriendPipe.observeSuccess().map(this::getRemoveFriendOperation),
            profileInteractor.addFriendToCirclesPipe().observeSuccess().map(this::getAddToCircleOperation),
            profileInteractor.removeFriendFromCirclesPipe().observeSuccess().map(this::getRemoveFromCircleOperation))
   }

   fun observeOnUpdateStorage() = observeCommands().flatMap {
      getStoragePipe().createObservableResult(createStorageCommand(it))
   }

   fun observeOnGetCirclesCommand() = friendInteractor.getCircleDecoratorPipe.observeWithReplay()

   fun getCircles(changeCircleAction: (circles: List<Circle>) -> Unit) {
      friendInteractor.getCircleDecoratorPipe.send(GetCirclesDecoratorCommand(circlesInteractor.pipe, changeCircleAction))
   }

   fun removeFriend(removeFriendCommand: RemoveFriendCommand) {
      friendInteractor.removeFriendPipe.send(removeFriendCommand)
   }

   fun addFriend(user: User, circle: Circle) {
      friendInteractor.addFriendPipe.send(AddFriendCommand(user, circle.id))
   }

   fun changeUserCircle(user: User, circle: Circle) {
      profileInteractor.addFriendToCirclesPipe().send(AddFriendToCircleCommand(circle, user))
   }

   fun removeUserFromCircle(user: User, circle: Circle) {
      profileInteractor.removeFriendFromCirclesPipe().send(RemoveFriendFromCircleCommand(circle, user))
   }

   protected abstract fun createStorageCommand(storageOperation: BaseUserStorageOperation): SC

   protected abstract fun getStoragePipe(): ActionPipe<SC>

   protected abstract fun getPaginationPipe(): ActionPipe<PC>

   protected open fun getUpdateListOperation(resultCommand: BaseUserPaginationCommand): BaseUserStorageOperation
         = DefaultUpdateListOperation(resultCommand.result, resultCommand.refresh)

   protected open fun getAddFriendOperation(resultCommand: AddFriendCommand): BaseUserStorageOperation
         = DefaultAddFriendOperation(resultCommand.result)

   protected open fun getRemoveFriendOperation(resultCommand: RemoveFriendCommand): BaseUserStorageOperation
         = DefaultRemoveFriendOperation(resultCommand.result)

   protected open fun getAcceptAllOperation(resultCommand: AcceptAllFriendRequestsCommand): BaseUserStorageOperation
         = DefaultAcceptAllOperation()

   protected open fun getAcceptRequestOperation(resultCommand: ActOnFriendRequestCommand.Accept): BaseUserStorageOperation
         = DefaultAcceptRequestOperation(resultCommand.result)

   protected open fun getRejectRequestOperation(resultCommand: ActOnFriendRequestCommand.Reject): BaseUserStorageOperation
         = DefaultRejectRequestOperation(resultCommand.result)

   protected open fun getDeleteRequestOperation(resultCommand: DeleteFriendRequestCommand): BaseUserStorageOperation
         = DefaultDeleteRequestOperation(resultCommand.result)

   protected open fun getRemoveFromCircleOperation(resultCommand: RemoveFriendFromCircleCommand): BaseUserStorageOperation
         = DefaultChangeCircleOperation(resultCommand.userId, { it.apply { remove(resultCommand.circle) } })

   protected open fun getAddToCircleOperation(resultCommand: AddFriendToCircleCommand): BaseUserStorageOperation
         = DefaultChangeCircleOperation(resultCommand.userId, { it.apply { add(resultCommand.circle) } })
}

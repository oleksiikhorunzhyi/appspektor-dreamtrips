package com.worldventures.dreamtrips.social.service.users.friend.delegate

import com.worldventures.dreamtrips.social.service.users.base.delegate.BaseUserStorageDelegate
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsStorageInteractor
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation
import com.worldventures.dreamtrips.social.service.users.friend.command.GetMutualFriendsCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.GetMutualFriendsPaginationCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.MutualFriendsStorageCommand
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor
import rx.Observable

class MutualFriendsStorageDelegate(
      friendInteractor: FriendsInteractor,
      friendsStorageInteractor: FriendsStorageInteractor,
      circlesInteractor: CirclesInteractor,
      profileInteractor: ProfileInteractor
) : BaseUserStorageDelegate<GetMutualFriendsPaginationCommand, MutualFriendsStorageCommand>(
      friendInteractor,
      friendsStorageInteractor,
      circlesInteractor,
      profileInteractor
) {

   private lateinit var storageKey: String

   fun observeOnUpdateStorage(storageKey: String): Observable<MutualFriendsStorageCommand> {
      this.storageKey = storageKey
      return super.observeOnUpdateStorage()
   }

   fun loadMutualFriends(reload: Boolean, createCommandAction: (Int, Int) -> GetMutualFriendsCommand) {
      getPaginationPipe().send(GetMutualFriendsPaginationCommand(storageKey, reload) { page, perPage ->
         friendInteractor.mutualFriendsPipe.createObservableResult(createCommandAction.invoke(page, perPage))
      })
   }

   override fun createStorageCommand(storageOperation: BaseUserStorageOperation)
         = MutualFriendsStorageCommand(storageKey, storageOperation)

   override fun getStoragePipe() = friendsStorageInteractor.mutualFriendsStoragePipe

   override fun getPaginationPipe() = friendsStorageInteractor.mutualFriendsPaginationPipe
}

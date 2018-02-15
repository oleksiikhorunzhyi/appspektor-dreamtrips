package com.worldventures.dreamtrips.social.service.users.search.delegate

import com.worldventures.dreamtrips.social.service.profile.ProfileInteractor
import com.worldventures.dreamtrips.social.service.users.base.delegate.BaseUserStorageDelegate
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsStorageInteractor
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation
import com.worldventures.dreamtrips.social.service.users.search.command.GetSearchUsersCommand
import com.worldventures.dreamtrips.social.service.users.search.command.SearchUsersPaginationCommand
import com.worldventures.dreamtrips.social.service.users.search.command.SearchedUsersStorageCommand

class SearchedUsersStorageDelegate(
      friendInteractor: FriendsInteractor,
      friendsStorageInteractor: FriendsStorageInteractor,
      circlesInteractor: CirclesInteractor,
      profileInteractor: ProfileInteractor
) : BaseUserStorageDelegate<SearchUsersPaginationCommand, SearchedUsersStorageCommand>(
      friendInteractor,
      friendsStorageInteractor,
      circlesInteractor,
      profileInteractor
) {

   fun searchUsers(query: String, reload: Boolean) {
      getPaginationPipe().send(SearchUsersPaginationCommand(reload, query) { page, perPage ->
         friendInteractor.searchUsersPipe.cancelLatest()
         friendInteractor.searchUsersPipe.createObservableResult(GetSearchUsersCommand(query, page, perPage))
      })
   }

   override fun createStorageCommand(storageOperation: BaseUserStorageOperation) = SearchedUsersStorageCommand(storageOperation)

   override fun getStoragePipe() = friendsStorageInteractor.searchedUsersStoragePipe

   override fun getPaginationPipe() = friendsStorageInteractor.searchedUsersPaginationPipe

}

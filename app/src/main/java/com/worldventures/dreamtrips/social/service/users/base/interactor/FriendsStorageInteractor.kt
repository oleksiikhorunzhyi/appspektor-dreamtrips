package com.worldventures.dreamtrips.social.service.users.base.interactor

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.service.users.friend.command.FriendListStorageCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.GetFriendsPaginationCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.GetMutualFriendsPaginationCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.MutualFriendsStorageCommand
import com.worldventures.dreamtrips.social.service.users.liker.command.GetLikersPaginationCommand
import com.worldventures.dreamtrips.social.service.users.liker.command.LikerStorageCommand
import com.worldventures.dreamtrips.social.service.users.request.command.GetRequestsPaginationCommand
import com.worldventures.dreamtrips.social.service.users.request.command.SortRequestsStorageCommand
import com.worldventures.dreamtrips.social.service.users.request.command.UserRequestsStorageCommand
import com.worldventures.dreamtrips.social.service.users.search.command.SearchUsersPaginationCommand
import com.worldventures.dreamtrips.social.service.users.search.command.SearchedUsersStorageCommand
import rx.schedulers.Schedulers.io

class FriendsStorageInteractor(creator: SessionActionPipeCreator) {
   val friendsListStoragePipe = creator.createPipe(FriendListStorageCommand::class.java, io())
   val searchedUsersStoragePipe = creator.createPipe(SearchedUsersStorageCommand::class.java, io())
   val userRequestsStoragePipe = creator.createPipe(UserRequestsStorageCommand::class.java, io())
   val mutualFriendsStoragePipe = creator.createPipe(MutualFriendsStorageCommand::class.java, io())
   val likersStoragePipe = creator.createPipe(LikerStorageCommand::class.java, io())
   val sortRequestsPipe = creator.createPipe(SortRequestsStorageCommand::class.java, io())
   val friendsListPaginationPipe = creator.createPipe(GetFriendsPaginationCommand::class.java, io())
   val searchedUsersPaginationPipe = creator.createPipe(SearchUsersPaginationCommand::class.java, io())
   val userRequestsPaginationPipe = creator.createPipe(GetRequestsPaginationCommand::class.java, io())
   val mutualFriendsPaginationPipe = creator.createPipe(GetMutualFriendsPaginationCommand::class.java, io())
   val likersPaginationPipe = creator.createPipe(GetLikersPaginationCommand::class.java, io())
}

package com.worldventures.dreamtrips.social.service.friends.interactor

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.service.friends.interactor.command.AcceptAllFriendRequestsCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.ActOnFriendRequestCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.AddFriendCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.DeleteFriendRequestCommand
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetFriendsCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetLikersCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetMutualFriendsCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetRequestsCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetSearchUsersCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.UserPaginationCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.RemoveFriendCommand
import com.worldventures.dreamtrips.social.service.friends.interactor.command.ChangeCirclesCommand
import com.worldventures.dreamtrips.social.service.friends.storage.command.UserStorageCommand

import javax.inject.Inject

import rx.schedulers.Schedulers

class FriendsInteractor @Inject constructor(sessionActionPipeCreator: SessionActionPipeCreator) {
   val deleteRequestPipe = sessionActionPipeCreator.createPipe(DeleteFriendRequestCommand::class.java, Schedulers.io())
   val acceptRequestPipe = sessionActionPipeCreator.createPipe(ActOnFriendRequestCommand.Accept::class.java, Schedulers.io())
   val rejectRequestPipe = sessionActionPipeCreator.createPipe(ActOnFriendRequestCommand.Reject::class.java, Schedulers.io())
   val acceptAllPipe = sessionActionPipeCreator.createPipe(AcceptAllFriendRequestsCommand::class.java, Schedulers.io())
   val removeFriendPipe = sessionActionPipeCreator.createPipe(RemoveFriendCommand::class.java, Schedulers.io())
   val addFriendPipe = sessionActionPipeCreator.createPipe(AddFriendCommand::class.java, Schedulers.io())
   val friendsPipe = sessionActionPipeCreator.createPipe(GetFriendsCommand::class.java, Schedulers.io())
   val likersPipe = sessionActionPipeCreator.createPipe(GetLikersCommand::class.java, Schedulers.io())
   val mutualFriendsPipe = sessionActionPipeCreator.createPipe(GetMutualFriendsCommand::class.java, Schedulers.io())
   val searchUsersPipe = sessionActionPipeCreator.createPipe(GetSearchUsersCommand::class.java, Schedulers.io())
   val requestsPipe = sessionActionPipeCreator.createPipe(GetRequestsCommand::class.java, Schedulers.io())
   val userPaginationPipe = sessionActionPipeCreator.createPipe(UserPaginationCommand::class.java)
   val storageCommand = sessionActionPipeCreator.createPipe(UserStorageCommand::class.java)
   val changeCirclePipe = sessionActionPipeCreator.createPipe(ChangeCirclesCommand::class.java)
}

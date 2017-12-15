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
import com.worldventures.dreamtrips.social.service.friends.interactor.command.RemoveFriendCommand

import javax.inject.Inject

import io.techery.janet.ActionPipe
import rx.schedulers.Schedulers

class FriendsInteractor @Inject constructor(sessionActionPipeCreator: SessionActionPipeCreator) {
   val deleteRequestPipe: ActionPipe<DeleteFriendRequestCommand>
         = sessionActionPipeCreator.createPipe(DeleteFriendRequestCommand::class.java, Schedulers.io())
   val acceptRequestPipe: ActionPipe<ActOnFriendRequestCommand.Accept>
         = sessionActionPipeCreator.createPipe(ActOnFriendRequestCommand.Accept::class.java, Schedulers.io())
   val rejectRequestPipe: ActionPipe<ActOnFriendRequestCommand.Reject>
         = sessionActionPipeCreator.createPipe(ActOnFriendRequestCommand.Reject::class.java, Schedulers.io())
   val acceptAllPipe: ActionPipe<AcceptAllFriendRequestsCommand>
         = sessionActionPipeCreator.createPipe(AcceptAllFriendRequestsCommand::class.java, Schedulers.io())
   val removeFriendPipe: ActionPipe<RemoveFriendCommand>
         = sessionActionPipeCreator.createPipe(RemoveFriendCommand::class.java, Schedulers.io())
   val addFriendPipe: ActionPipe<AddFriendCommand>
         = sessionActionPipeCreator.createPipe(AddFriendCommand::class.java, Schedulers.io())
   val friendsPipe: ActionPipe<GetFriendsCommand>
         = sessionActionPipeCreator.createPipe(GetFriendsCommand::class.java, Schedulers.io())
   val likersPipe: ActionPipe<GetLikersCommand>
         = sessionActionPipeCreator.createPipe(GetLikersCommand::class.java, Schedulers.io())
   val mutualFriendsPipe: ActionPipe<GetMutualFriendsCommand>
         = sessionActionPipeCreator.createPipe(GetMutualFriendsCommand::class.java, Schedulers.io())
   val searchUsersPipe: ActionPipe<GetSearchUsersCommand>
         = sessionActionPipeCreator.createPipe(GetSearchUsersCommand::class.java, Schedulers.io())
   val requestsPipe: ActionPipe<GetRequestsCommand>
         = sessionActionPipeCreator.createPipe(GetRequestsCommand::class.java, Schedulers.io())
}

package com.worldventures.dreamtrips.social.service.users.base.interactor

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesDecoratorCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.GetFriendsCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.GetMutualFriendsCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.RemoveFriendCommand
import com.worldventures.dreamtrips.social.service.users.liker.command.GetLikersCommand
import com.worldventures.dreamtrips.social.service.users.request.command.AcceptAllFriendRequestsCommand
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.request.command.DeleteFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.request.command.GetRequestsCommand
import com.worldventures.dreamtrips.social.service.users.search.command.AddFriendCommand
import com.worldventures.dreamtrips.social.service.users.search.command.GetSearchUsersCommand
import rx.schedulers.Schedulers

class FriendsInteractor(sessionActionPipeCreator: SessionActionPipeCreator) {
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
   val getCircleDecoratorPipe = sessionActionPipeCreator.createPipe(GetCirclesDecoratorCommand::class.java)
}

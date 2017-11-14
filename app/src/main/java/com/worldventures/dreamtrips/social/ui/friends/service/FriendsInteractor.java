package com.worldventures.dreamtrips.social.ui.friends.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.friends.service.command.AcceptAllFriendRequestsCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.AddFriendCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.DeleteFriendRequestCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetFriendsCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetLikersCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetMutualFriendsCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetRequestsCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetSearchUsersCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.RemoveFriendCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class FriendsInteractor {

   private final ActionPipe<DeleteFriendRequestCommand> deleteRequestPipe;
   private final ActionPipe<ActOnFriendRequestCommand.Accept> acceptRequestPipe;
   private final ActionPipe<ActOnFriendRequestCommand.Reject> rejectRequestPipe;
   private final ActionPipe<AcceptAllFriendRequestsCommand> acceptAllPipe;
   private final ActionPipe<RemoveFriendCommand> removeFriendPipe;
   private final ActionPipe<AddFriendCommand> addFriendPipe;
   private final ActionPipe<GetFriendsCommand> getFriendsPipe;
   private final ActionPipe<GetLikersCommand> getLikersPipe;
   private final ActionPipe<GetMutualFriendsCommand> getMutualFriendsPipe;
   private final ActionPipe<GetSearchUsersCommand> getSearchUsersPipe;
   private final ActionPipe<GetRequestsCommand> getRequestsPipe;

   @Inject
   public FriendsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      deleteRequestPipe = sessionActionPipeCreator.createPipe(DeleteFriendRequestCommand.class, Schedulers.io());
      acceptRequestPipe = sessionActionPipeCreator.createPipe(ActOnFriendRequestCommand.Accept.class, Schedulers.io());
      rejectRequestPipe = sessionActionPipeCreator.createPipe(ActOnFriendRequestCommand.Reject.class, Schedulers.io());
      acceptAllPipe = sessionActionPipeCreator.createPipe(AcceptAllFriendRequestsCommand.class, Schedulers.io());
      addFriendPipe = sessionActionPipeCreator.createPipe(AddFriendCommand.class, Schedulers.io());
      removeFriendPipe = sessionActionPipeCreator.createPipe(RemoveFriendCommand.class, Schedulers.io());
      getFriendsPipe = sessionActionPipeCreator.createPipe(GetFriendsCommand.class, Schedulers.io());
      getLikersPipe = sessionActionPipeCreator.createPipe(GetLikersCommand.class, Schedulers.io());
      getMutualFriendsPipe = sessionActionPipeCreator.createPipe(GetMutualFriendsCommand.class, Schedulers.io());
      getSearchUsersPipe = sessionActionPipeCreator.createPipe(GetSearchUsersCommand.class, Schedulers.io());
      getRequestsPipe = sessionActionPipeCreator.createPipe(GetRequestsCommand.class, Schedulers.io());
   }

   public ActionPipe<DeleteFriendRequestCommand> deleteRequestPipe() {
      return deleteRequestPipe;
   }

   public ActionPipe<ActOnFriendRequestCommand.Accept> acceptRequestPipe() {
      return acceptRequestPipe;
   }

   public ActionPipe<ActOnFriendRequestCommand.Reject> rejectRequestPipe() {
      return rejectRequestPipe;
   }

   public ActionPipe<AcceptAllFriendRequestsCommand> acceptAllPipe() {
      return acceptAllPipe;
   }

   public ActionPipe<AddFriendCommand> addFriendPipe() {
      return addFriendPipe;
   }

   public ActionPipe<RemoveFriendCommand> removeFriendPipe() {
      return removeFriendPipe;
   }

   public ActionPipe<GetFriendsCommand> getFriendsPipe() {
      return getFriendsPipe;
   }

   public ActionPipe<GetLikersCommand> getLikersPipe() {
      return getLikersPipe;
   }

   public ActionPipe<GetMutualFriendsCommand> getMutualFriendsPipe() {
      return getMutualFriendsPipe;
   }

   public ActionPipe<GetSearchUsersCommand> getSearchUsersPipe() {
      return getSearchUsersPipe;
   }

   public ActionPipe<GetRequestsCommand> getRequestsPipe() {
      return getRequestsPipe;
   }
}

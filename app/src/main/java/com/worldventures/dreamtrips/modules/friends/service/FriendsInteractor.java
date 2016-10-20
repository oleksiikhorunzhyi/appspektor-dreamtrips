package com.worldventures.dreamtrips.modules.friends.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.friends.service.command.AcceptAllFriendRequestsCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.AddFriendCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.DeleteFriendRequestCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.GetFriendsCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.GetLikersCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.GetMutualFriendsCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.GetSearchUsersCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.RemoveFriendCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class FriendsInteractor {

   private ActionPipe<DeleteFriendRequestCommand> deleteRequestPipe;
   private ActionPipe<ActOnFriendRequestCommand.Accept> acceptRequestPipe;
   private ActionPipe<ActOnFriendRequestCommand.Reject> rejectRequestPipe;
   private ActionPipe<AcceptAllFriendRequestsCommand> acceptAllPipe;
   private ActionPipe<RemoveFriendCommand> removeFriendPipe;
   private ActionPipe<AddFriendCommand> addFriendPipe;
   private ActionPipe<GetFriendsCommand> getFriendsPipe;
   private ActionPipe<GetLikersCommand> getLikersPipe;
   private ActionPipe<GetMutualFriendsCommand> getMutualFriendsPipe;
   private ActionPipe<GetSearchUsersCommand> getSearchUsersPipe;

   @Inject
   public FriendsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      deleteRequestPipe = sessionActionPipeCreator.createPipe(DeleteFriendRequestCommand.class, Schedulers.io());
      acceptRequestPipe= sessionActionPipeCreator.createPipe(ActOnFriendRequestCommand.Accept.class, Schedulers.io());
      rejectRequestPipe= sessionActionPipeCreator.createPipe(ActOnFriendRequestCommand.Reject.class, Schedulers.io());
      acceptAllPipe = sessionActionPipeCreator.createPipe(AcceptAllFriendRequestsCommand.class, Schedulers.io());
      addFriendPipe = sessionActionPipeCreator.createPipe(AddFriendCommand.class, Schedulers.io());
      removeFriendPipe = sessionActionPipeCreator.createPipe(RemoveFriendCommand.class, Schedulers.io());
      getFriendsPipe = sessionActionPipeCreator.createPipe(GetFriendsCommand.class);
      getLikersPipe = sessionActionPipeCreator.createPipe(GetLikersCommand.class);
      getMutualFriendsPipe = sessionActionPipeCreator.createPipe(GetMutualFriendsCommand.class);
      getSearchUsersPipe = sessionActionPipeCreator.createPipe(GetSearchUsersCommand.class);
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
}

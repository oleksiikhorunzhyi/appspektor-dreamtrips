package com.worldventures.dreamtrips.modules.friends.janet;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;

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

   @Inject
   public FriendsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      deleteRequestPipe = sessionActionPipeCreator.createPipe(DeleteFriendRequestCommand.class, Schedulers.io());
      acceptRequestPipe= sessionActionPipeCreator.createPipe(ActOnFriendRequestCommand.Accept.class, Schedulers.io());
      rejectRequestPipe= sessionActionPipeCreator.createPipe(ActOnFriendRequestCommand.Reject.class, Schedulers.io());
      acceptAllPipe = sessionActionPipeCreator.createPipe(AcceptAllFriendRequestsCommand.class, Schedulers.io());
      addFriendPipe = sessionActionPipeCreator.createPipe(AddFriendCommand.class, Schedulers.io());
      removeFriendPipe = sessionActionPipeCreator.createPipe(RemoveFriendCommand.class, Schedulers.io());
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
}

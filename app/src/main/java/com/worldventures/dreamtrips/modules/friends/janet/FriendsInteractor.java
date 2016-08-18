package com.worldventures.dreamtrips.modules.friends.janet;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
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
   public FriendsInteractor(Janet janet) {
      deleteRequestPipe = janet.createPipe(DeleteFriendRequestCommand.class, Schedulers.io());
      acceptRequestPipe= janet.createPipe(ActOnFriendRequestCommand.Accept.class, Schedulers.io());
      rejectRequestPipe= janet.createPipe(ActOnFriendRequestCommand.Reject.class, Schedulers.io());
      acceptAllPipe = janet.createPipe(AcceptAllFriendRequestsCommand.class, Schedulers.io());
      addFriendPipe = janet.createPipe(AddFriendCommand.class, Schedulers.io());
      removeFriendPipe = janet.createPipe(RemoveFriendCommand.class, Schedulers.io());
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

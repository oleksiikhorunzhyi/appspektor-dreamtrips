package com.messenger.delegate.chat.flagging;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

public class FlagMessageDelegate {

   private final ActionPipe<FlagMessageCommand> flaggingPipe;

   public FlagMessageDelegate(Janet janet) {
      flaggingPipe = janet.createPipe(FlagMessageCommand.class, Schedulers.io());
   }

   public void flagMessage(FlagMessageDTO flagMessageDTO) {
      flaggingPipe.send(new FlagMessageCommand(flagMessageDTO));
   }

   public Observable<ActionState<FlagMessageCommand>> observeOngoingFlagging() {
      return flaggingPipe.observeWithReplay();
   }

   public void clearReplays() {
      flaggingPipe.clearReplays();
   }
}

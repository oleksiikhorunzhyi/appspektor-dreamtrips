package com.worldventures.dreamtrips.wallet.service.command;

import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public final class DefaultCardIdCommand extends CachedValueCommand<String> {

   public static DefaultCardIdCommand set(String cardId) {
      return new DefaultCardIdCommand(cache -> cardId);
   }

   public DefaultCardIdCommand() {
   }

   public DefaultCardIdCommand(Func1<String, String> operationFunc) {
      super(operationFunc);
   }

}

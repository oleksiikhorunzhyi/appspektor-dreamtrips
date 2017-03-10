package com.worldventures.dreamtrips.wallet.service.command;

import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public final class DefaultRecordIdCommand extends CachedValueCommand<String> {

   public static DefaultRecordIdCommand set(String recordId) {
      return new DefaultRecordIdCommand(cache -> recordId);
   }

   public DefaultRecordIdCommand() {
   }

   public DefaultRecordIdCommand(Func1<String, String> operationFunc) {
      super(operationFunc);
   }

}

package com.worldventures.dreamtrips.wallet.service.command.record;

import com.worldventures.dreamtrips.wallet.service.command.CachedValueCommand;

import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public final class DefaultRecordIdCommand extends CachedValueCommand<String> {

   public static DefaultRecordIdCommand set(String recordId) {
      return new DefaultRecordIdCommand(cache -> recordId);
   }

   public static DefaultRecordIdCommand fetch() {
      return new DefaultRecordIdCommand(cache -> cache);
   }

   private DefaultRecordIdCommand(Func1<String, String> operationFunc) {
      super(operationFunc);
   }
}

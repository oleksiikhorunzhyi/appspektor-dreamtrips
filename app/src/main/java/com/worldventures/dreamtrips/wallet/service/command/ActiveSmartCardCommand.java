package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.util.NoActiveSmartCardException;

import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public class ActiveSmartCardCommand extends CachedValueCommand<SmartCard> {

   public ActiveSmartCardCommand() {
      this(smartCard -> smartCard);
   }

   public ActiveSmartCardCommand(Func1<SmartCard, SmartCard> operationFunc) {
      super(smartCard -> {
         if (smartCard == null) {
            throw new NoActiveSmartCardException("Active Smart Card does not exist.");
         }
         return operationFunc.call(smartCard);
      });
   }

   public ActiveSmartCardCommand(SmartCard smartCard) {
      super(cache -> smartCard);
   }
}

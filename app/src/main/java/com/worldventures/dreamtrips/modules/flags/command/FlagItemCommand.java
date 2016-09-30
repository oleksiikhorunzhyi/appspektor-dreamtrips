package com.worldventures.dreamtrips.modules.flags.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.flagging.FlagItemHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.FlagData;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FlagItemCommand extends CommandWithError<FlagData> implements InjectableAction {

   private FlagData flagData;

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   public FlagItemCommand(FlagData flagData) {
      this.flagData = flagData;
   }

   @Override
   protected void run(CommandCallback<FlagData> callback) throws Throwable {
      janet.createPipe(FlagItemHttpAction.class)
            .createObservableResult(new FlagItemHttpAction(flagData.uid, flagData.flagReasonId, flagData.reason))
            .map(flagItemHttpAction -> flagData)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_flag_item;
   }
}

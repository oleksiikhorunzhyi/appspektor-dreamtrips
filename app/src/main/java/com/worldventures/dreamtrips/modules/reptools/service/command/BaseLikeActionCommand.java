package com.worldventures.dreamtrips.modules.reptools.service.command;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;

public abstract class BaseLikeActionCommand<Action extends AuthorizedHttpAction>
      extends CommandWithError implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   protected int id;

   public BaseLikeActionCommand(int id) {
      this.id = id;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(getActionClass())
            .createObservableResult(getAction())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   abstract Action getAction();

   abstract Class<Action> getActionClass();
}

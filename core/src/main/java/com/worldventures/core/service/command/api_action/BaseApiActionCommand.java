package com.worldventures.core.service.command.api_action;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Janet;

public abstract class BaseApiActionCommand<HttpAction extends com.worldventures.dreamtrips.api.api_common.BaseHttpAction, T, R>
      extends CommandWithError<T> implements InjectableAction {

   @Inject Janet janet;

   @Override
   protected void run(CommandCallback<T> callback) throws Throwable {
      janet.createPipe(getHttpActionClass())
            .createObservableResult(getHttpAction())
            .map(this::mapHttpActionResult)
            .map(this::mapCommandResult)
            .subscribe(t -> onSuccess(callback, t), callback::onFail);
   }

   protected void onSuccess(CommandCallback<T> callback, T t) {
      callback.onSuccess(t);
   }

   protected abstract R mapHttpActionResult(HttpAction httpAction);

   protected T mapCommandResult(R httpCommandResult) {
      return (T) httpCommandResult;
   }

   protected abstract HttpAction getHttpAction();

   protected abstract Class<HttpAction> getHttpActionClass();
}

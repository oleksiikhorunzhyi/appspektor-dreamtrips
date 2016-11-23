package com.worldventures.dreamtrips.core.api.action;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;

public abstract class BaseApiActionCommand<HttpAction extends com.worldventures.dreamtrips.api.api_common.BaseHttpAction, T, R>
      extends CommandWithError<T> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   @Override
   protected void run(CommandCallback<T> callback) throws Throwable {
      janet.createPipe(getHttpActionClass())
            .createObservableResult(getHttpAction())
            .map(this::mapHttpActionResult)
            .map(this::mapCommandResult)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   protected abstract R mapHttpActionResult(HttpAction httpAction);

   protected T mapCommandResult(R httpCommandResult) {
      return (T) httpCommandResult;
   }

   protected abstract HttpAction getHttpAction();

   protected abstract Class<HttpAction> getHttpActionClass();
}

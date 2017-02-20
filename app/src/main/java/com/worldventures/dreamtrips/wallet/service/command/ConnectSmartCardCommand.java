package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.ImmutableConnectionParams;
import rx.Observable;
import timber.log.Timber;

@CommandAction
public class ConnectSmartCardCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;

   private final String smartCardId;
   private final boolean waitForParing;

   public ConnectSmartCardCommand(String smartCardId, boolean waitForParing) {
      this.smartCardId = smartCardId;
      this.waitForParing = waitForParing;
   }

   @Override
   @Deprecated
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable<ConnectAction> observable = janet.createPipe(ConnectAction.class)
            .createObservableResult(new ConnectAction(ImmutableConnectionParams.of((int) Long.parseLong(smartCardId))));

      if (waitForParing) {
         observable = observable.delay(20L, TimeUnit.SECONDS); //TODO: Hard code for waiting typing PIN
      }

      observable.subscribe(value -> callback.onSuccess(null),
            throwable -> {
               Timber.e(throwable, "Error while connecting to smart card");
               callback.onSuccess(null);
            }
      );
   }
}

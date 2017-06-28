package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.ImmutableConnectionParams;
import timber.log.Timber;

@CommandAction
public class ConnectSmartCardCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;

   private final String smartCardId;

   public ConnectSmartCardCommand(String smartCardId, boolean waitForParing) {
      this.smartCardId = smartCardId;
   }

   public ConnectSmartCardCommand(String smartCardId) {
      this(smartCardId, false);
   }

   @Override
   @Deprecated
   protected void run(CommandCallback<Void> callback) throws Throwable {
      janet.createPipe(ConnectAction.class)
            .createObservableResult(new ConnectAction(ImmutableConnectionParams.of(Long.parseLong(smartCardId))))
            .subscribe(value -> callback.onSuccess(null),
                  throwable -> {
                     Timber.e(throwable, "Error while connecting to smart card");
                     callback.onSuccess(null);
                  }
            );
   }
}

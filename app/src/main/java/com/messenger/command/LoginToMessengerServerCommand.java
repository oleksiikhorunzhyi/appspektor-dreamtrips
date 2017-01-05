package com.messenger.command;

import com.messenger.messengerservers.ConnectionStatus;
import com.messenger.synchmechanism.MessengerConnector;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import timber.log.Timber;

@CommandAction
public class LoginToMessengerServerCommand extends Command<Void> implements InjectableAction {

   @Inject MessengerConnector messengerConnector;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      messengerConnector.disconnect();
      messengerConnector.getAuthToServerStatus()
            .filter(connectionStatus -> connectionStatus == ConnectionStatus.DISCONNECTED)
            .take(1)
            .subscribe(connectionStatus -> messengerConnector.connect(),
                  throwable -> Timber.e(throwable, "Error while reconnect to XMMP Server"));
   }
}

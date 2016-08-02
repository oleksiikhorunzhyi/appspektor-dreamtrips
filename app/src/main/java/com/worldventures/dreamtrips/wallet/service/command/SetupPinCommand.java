package com.worldventures.dreamtrips.wallet.service.command;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SetupPinCommand extends Command<Void> {

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        Thread.sleep(5000);
        callback.onSuccess(null);
    }
}

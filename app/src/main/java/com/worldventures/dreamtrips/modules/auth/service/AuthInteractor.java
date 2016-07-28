package com.worldventures.dreamtrips.modules.auth.service;

import com.worldventures.dreamtrips.modules.auth.service.command.UnsubribeFromPushCommand;
import com.worldventures.dreamtrips.modules.auth.service.command.UpdateAuthInfoCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class AuthInteractor {

    protected ActionPipe<UpdateAuthInfoCommand> updateAuthInfoCommandActionPipe;
    protected ActionPipe<UnsubribeFromPushCommand> unsubribeFromPushPipe;

    public AuthInteractor(Janet janet) {
        updateAuthInfoCommandActionPipe = janet.createPipe(UpdateAuthInfoCommand.class, Schedulers.io());
        unsubribeFromPushPipe = janet.createPipe(UnsubribeFromPushCommand.class, Schedulers.io());
    }

    public ActionPipe<UnsubribeFromPushCommand> unsubribeFromPushPipe() {
        return unsubribeFromPushPipe;
    }

    public ActionPipe<UpdateAuthInfoCommand> updateAuthInfoCommandActionPipe() {
        return updateAuthInfoCommandActionPipe;
    }
}

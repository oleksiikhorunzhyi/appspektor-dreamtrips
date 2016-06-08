package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.modules.common.api.janet.command.UpdateAuthInfoCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class AuthInteractor {

    protected ActionPipe<UpdateAuthInfoCommand> pipe;

    public AuthInteractor(Janet janet) {
        pipe = janet.createPipe(UpdateAuthInfoCommand.class);
    }

    public ActionPipe<UpdateAuthInfoCommand> pipe() {
        return pipe;
    }
}

package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.modules.common.api.janet.command.LocalesCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class LocalesInteractor {

    ActionPipe<LocalesCommand> pipe;

    public LocalesInteractor(Janet janet) {
        this.pipe = janet.createPipe(LocalesCommand.class);
    }

    public ActionPipe<LocalesCommand> pipe() {
        return pipe;
    }
}

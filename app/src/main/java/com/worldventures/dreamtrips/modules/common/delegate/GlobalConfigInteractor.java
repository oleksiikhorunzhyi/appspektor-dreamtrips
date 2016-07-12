package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.modules.common.api.janet.command.GlobalConfigCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class GlobalConfigInteractor {

    protected ActionPipe<GlobalConfigCommand> configPipe;

    public GlobalConfigInteractor(Janet janet) {
        this.configPipe = janet.createPipe(GlobalConfigCommand.class);
    }

    public ActionPipe<GlobalConfigCommand> pipe() {
        return configPipe;
    }
}

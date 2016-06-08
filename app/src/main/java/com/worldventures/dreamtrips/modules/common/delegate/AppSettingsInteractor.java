package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.modules.common.api.janet.command.AppSettingsCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class AppSettingsInteractor {

    ActionPipe<AppSettingsCommand> configPipe;

    public AppSettingsInteractor(Janet janet) {
        this.configPipe = janet.createPipe(AppSettingsCommand.class);
    }

    public ActionPipe<AppSettingsCommand> pipe() {
        return configPipe;
    }


}

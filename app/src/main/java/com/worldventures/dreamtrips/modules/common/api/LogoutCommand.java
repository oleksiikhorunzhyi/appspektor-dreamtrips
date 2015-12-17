package com.worldventures.dreamtrips.modules.common.api;

import com.worldventures.dreamtrips.core.api.request.Command;

public class LogoutCommand extends Command<Void> {

    public LogoutCommand() {
        super(Void.class);
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().logout();
    }
}

package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Command;

public class FlagItemCommand extends Command<Void> {
    private String uid;
    private String nameOfReason;

    public FlagItemCommand(String uid, String nameOfReason) {
        super(Void.class);
        this.uid = uid;
        this.nameOfReason = nameOfReason;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().flagItem(uid, nameOfReason);
    }
}

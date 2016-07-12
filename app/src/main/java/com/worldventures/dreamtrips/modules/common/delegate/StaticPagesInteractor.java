package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.modules.common.api.janet.command.StaticPageConfigCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class StaticPagesInteractor {

    ActionPipe<StaticPageConfigCommand> pipe;

    public StaticPagesInteractor(Janet janet) {
        this.pipe = janet.createPipe(StaticPageConfigCommand.class);
    }

    public ActionPipe<StaticPageConfigCommand> pipe() {
        return pipe;
    }
}

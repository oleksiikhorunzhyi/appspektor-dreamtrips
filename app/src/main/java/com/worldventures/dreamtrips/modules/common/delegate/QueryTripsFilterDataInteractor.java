package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.modules.common.api.janet.command.TripsFilterDataCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class QueryTripsFilterDataInteractor {

    ActionPipe<TripsFilterDataCommand> pipe;

    public QueryTripsFilterDataInteractor(Janet janet) {
        this.pipe = janet.createPipe(TripsFilterDataCommand.class);
    }

    public ActionPipe<TripsFilterDataCommand> pipe() {
        return pipe;
    }
}


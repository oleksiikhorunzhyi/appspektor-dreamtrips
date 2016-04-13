package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlLocationCommand extends CallableCommand<DtlLocation> {

    private boolean fromDB;

    public DtlLocationCommand(SnappyRepository db) {
        super(db::getDtlLocation);
        fromDB = true;
    }

    public boolean isResultDefined() {
        return getResult() != null && getResult().getLocationSourceType() != LocationSourceType.UNDEFINED;
    }

    public DtlLocationCommand(DtlLocation location) {
        super(() -> location);
    }

    public boolean isFromDB() {
        return fromDB;
    }


}

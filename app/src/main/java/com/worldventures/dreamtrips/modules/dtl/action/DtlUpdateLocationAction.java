package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.api.action.CallableCommandAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlUpdateLocationAction extends CallableCommandAction<DtlLocation> {
    public DtlUpdateLocationAction(SnappyRepository db, DtlLocation location) {
        super(() -> {
            if (location == null
                    || location.getLocationSourceType() == LocationSourceType.UNDEFINED) {
                db.cleanDtlLocation();
            } else {
                db.saveDtlLocation(location);
                db.cleanLastMapCameraPosition(); // need clean last map camera position
            }
            return location;
        });
    }
}

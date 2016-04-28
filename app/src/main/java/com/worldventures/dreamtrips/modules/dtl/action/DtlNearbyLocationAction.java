package com.worldventures.dreamtrips.modules.dtl.action;

import android.location.Location;

import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.api.action.CallableCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlNearbyLocationAction extends CallableCommandAction<List<DtlExternalLocation>> {

    public DtlNearbyLocationAction(DtlApi dtlApi, Location location) {
        super(() -> dtlApi.getNearbyLocations(location.getLatitude() + ","
                + location.getLongitude()));
    }
}

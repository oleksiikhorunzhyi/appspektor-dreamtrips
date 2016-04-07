package com.worldventures.dreamtrips.modules.dtl.model.location;

import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.trips.model.Location;

public interface DtlLocation {

    LocationSourceType getLocationSourceType();

    String getLongName();

    Location getCoordinates();

    String getAnalyticsName();
}

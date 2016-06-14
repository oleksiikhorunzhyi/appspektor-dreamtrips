package com.worldventures.dreamtrips.modules.dtl.model.location;

import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.trips.model.Location;

public interface DtlLocation {

    LocationSourceType getLocationSourceType();

    String getLongName();

    Location getCoordinates();

    String getAnalyticsName();

    DtlLocation UNDEFINED = new DtlLocation() {
        @Override
        public LocationSourceType getLocationSourceType() {
            return LocationSourceType.UNDEFINED;
        }

        @Override
        public String getLongName() {
            return null;
        }

        @Override
        public com.worldventures.dreamtrips.modules.trips.model.Location getCoordinates() {
            return new Location(0, 0);
        }

        @Override
        public String getAnalyticsName() {
            return ""; //TODO: need to set specific name for situations when location is undefined.
        }
    };
}

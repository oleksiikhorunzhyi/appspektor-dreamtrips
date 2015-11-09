package com.worldventures.dreamtrips.modules.dtl.model.adapter;

import com.worldventures.dreamtrips.core.gson.CustomizedTypeAdapterFactory;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.trips.model.Location;

public class DtlLocationAdapterFactory extends CustomizedTypeAdapterFactory<DtlLocation> {

    public DtlLocationAdapterFactory() {
        super(DtlLocation.class);
    }

    @Override
    protected void afterParsed(DtlLocation parsed) {
        super.afterParsed(parsed);
        if (parsed.getCoordinates() == null)
            parsed.setCoordinates(new Location(0.0d, 0.0d));
    }
}

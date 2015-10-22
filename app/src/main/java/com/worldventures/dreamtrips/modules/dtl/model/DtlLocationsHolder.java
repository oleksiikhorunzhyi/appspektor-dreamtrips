package com.worldventures.dreamtrips.modules.dtl.model;

import com.innahema.collections.query.queriables.Queryable;

import java.util.List;

public class DtlLocationsHolder {

    List<DtlLocation> locations;
    List<DtlLocation> nearby;

    public List<DtlLocation> getLocations() {
        return locations;
    }

    public List<DtlLocation> getNearby() {
        return nearby;
    }

    public DtlLocationsHolder filter(String caption) {
        DtlLocationsHolder temp = new DtlLocationsHolder();
        temp.nearby = Queryable.from(nearby).filter(dtlLocation ->
                dtlLocation.getLongName().toLowerCase().contains(caption)).toList();
        temp.locations = Queryable.from(locations).filter(dtlLocation ->
                dtlLocation.getLongName().toLowerCase().contains(caption)).toList();
        return temp;
    }
}

package com.worldventures.dreamtrips.modules.dtl.model;

import com.innahema.collections.query.queriables.Queryable;

import java.util.List;

public class DtlLocationsHolder {

    private List<DtlLocation> cities;
    private List<DtlLocation> nearby;

    public List<DtlLocation> getCities() {
        return cities;
    }

    public List<DtlLocation> getNearby() {
        return nearby;
    }

    public DtlLocationsHolder filter(String caption) {
        DtlLocationsHolder temp = new DtlLocationsHolder();
        temp.nearby = Queryable.from(nearby).filter(dtlLocation ->
                dtlLocation.getName().toLowerCase().contains(caption) ||
                        dtlLocation.getCountryName().toLowerCase().contains(caption)).toList();
        temp.cities = Queryable.from(cities).filter(dtlLocation ->
                dtlLocation.getName().toLowerCase().contains(caption) ||
                        dtlLocation.getCountryName().toLowerCase().contains(caption)).toList();
        return temp;
    }
}

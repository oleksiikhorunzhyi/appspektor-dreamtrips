package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;

import java.util.ArrayList;

public class GetInspireMePhotosQuery extends Query<ArrayList<Inspiration>> {

    protected int perPage;
    protected int page;
    private double randomSeed;

    public GetInspireMePhotosQuery(int perPage, int page, double randomSeed) {
        super((Class<ArrayList<Inspiration>>) new ArrayList<Inspiration>().getClass());
        this.perPage = perPage;
        this.page = page;
        this.randomSeed = randomSeed;
    }

    @Override
    public ArrayList<Inspiration> loadDataFromNetwork() throws Exception {
        return getService().getInspirationsPhotos(perPage, page,randomSeed);
    }
}

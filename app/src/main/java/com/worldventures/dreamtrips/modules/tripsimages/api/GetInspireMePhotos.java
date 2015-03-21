package com.worldventures.dreamtrips.modules.tripsimages.api;

import android.util.Log;

import com.worldventures.dreamtrips.core.api.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;

import java.util.ArrayList;

public class GetInspireMePhotos extends DreamTripsRequest<ArrayList<Inspiration>> {

    int perPage;
    int page;

    public GetInspireMePhotos(int perPage, int page) {
        super((Class<ArrayList<Inspiration>>) new ArrayList<Inspiration>().getClass());
        this.perPage = perPage;
        this.page = page;
    }

    @Override
    public ArrayList<Inspiration> loadDataFromNetwork() throws Exception {
        Log.i("LoadNext", "per page: " + perPage + "; page:" + page);
        return getService().getInspirationsPhotos(perPage, page);
    }
}

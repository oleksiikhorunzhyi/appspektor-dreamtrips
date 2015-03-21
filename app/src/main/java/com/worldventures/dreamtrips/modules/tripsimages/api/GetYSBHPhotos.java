package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;

public class GetYSBHPhotos extends DreamTripsRequest<ArrayList<Photo>> {
    int perPage;
    int page;

    public GetYSBHPhotos(int perPage, int page) {
        super((Class<ArrayList<Photo>>) new ArrayList<Photo>().getClass());
        this.perPage = perPage;
        this.page = page;
    }

    @Override
    public ArrayList<Photo> loadDataFromNetwork() throws Exception {
        return getService().getYouShoulBeHerePhotos(perPage, page);
    }
}

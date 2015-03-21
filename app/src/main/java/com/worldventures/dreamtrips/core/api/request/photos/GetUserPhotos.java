package com.worldventures.dreamtrips.core.api.request.photos;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Photo;

import java.util.ArrayList;

public class GetUserPhotos extends DreamTripsRequest<ArrayList<Photo>> {

    int perPage;
    int page;

    public GetUserPhotos(int perPage, int page) {
        super((Class<ArrayList<Photo>>) new ArrayList<Photo>().getClass());
        this.perPage = perPage;
        this.page = page;
    }

    @Override
    public ArrayList<Photo> loadDataFromNetwork() throws Exception {
        return getService().getUserPhotos(perPage, page);
    }
}

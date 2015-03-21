package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;

public class GetYSBHPhotosQuery extends Query<ArrayList<Photo>> {
    int perPage;
    int page;

    public GetYSBHPhotosQuery(int perPage, int page) {
        super((Class<ArrayList<Photo>>) new ArrayList<Photo>().getClass());
        this.perPage = perPage;
        this.page = page;
    }

    @Override
    public ArrayList<Photo> loadDataFromNetwork() throws Exception {
        return getService().getYouShoulBeHerePhotos(perPage, page);
    }
}

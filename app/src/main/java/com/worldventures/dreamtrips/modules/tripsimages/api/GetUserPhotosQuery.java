package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;

public class GetUserPhotosQuery extends Query<ArrayList<IFullScreenObject>> {

    protected int perPage;
    protected  int page;

    public GetUserPhotosQuery(int perPage, int page) {
        super((Class<ArrayList<IFullScreenObject>>) new ArrayList<IFullScreenObject>().getClass());
        this.perPage = perPage;
        this.page = page;
    }

    @Override
    public ArrayList<IFullScreenObject> loadDataFromNetwork() throws Exception {
        ArrayList<Photo> photos = getService().getUsersPhotos(perPage, page);
        ArrayList<IFullScreenObject> result = new ArrayList<>();
        result.addAll(photos);
        return result;
    }
}

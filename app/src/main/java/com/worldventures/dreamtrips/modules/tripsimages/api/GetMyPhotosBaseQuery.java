package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;

public class GetMyPhotosBaseQuery extends Query<ArrayList<IFullScreenAvailableObject>> {

    protected int perPage;
    protected int page;
    protected int currentUserId;

    public GetMyPhotosBaseQuery(int currentUserId, int perPage, int page) {
        super((Class<ArrayList<IFullScreenAvailableObject>>) new ArrayList<IFullScreenAvailableObject>().getClass());
        this.currentUserId = currentUserId;
        this.perPage = perPage;
        this.page = page;
    }

    @Override
    public ArrayList<IFullScreenAvailableObject> loadDataFromNetwork() throws Exception {
        ArrayList<Photo> myPhotos = getService().getMyPhotos(currentUserId, perPage, page);
        ArrayList<IFullScreenAvailableObject> result = new ArrayList<>();
        result.addAll(myPhotos);
        return result;
    }

}

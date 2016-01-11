package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import java.util.ArrayList;

import javax.inject.Inject;

public class GetUserPhotosQuery extends Query<ArrayList<IFullScreenObject>> {

    protected int perPage;
    protected int page;
    protected int userId;

    public GetUserPhotosQuery(int userId, int perPage, int page) {
        super((Class<ArrayList<IFullScreenObject>>) new ArrayList<IFullScreenObject>().getClass());
        this.userId = userId;
        this.perPage = perPage;
        this.page = page;
    }

    @Override
    public ArrayList<IFullScreenObject> loadDataFromNetwork() throws Exception {
        ArrayList<IFullScreenObject> result = new ArrayList<>();
        result.addAll(getService().getUserPhotos(userId, perPage, page));
        return result;
    }
}

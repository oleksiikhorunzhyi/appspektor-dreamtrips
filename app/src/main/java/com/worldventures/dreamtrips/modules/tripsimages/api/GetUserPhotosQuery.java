package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManager;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import java.util.ArrayList;

public class GetUserPhotosQuery extends Query<ArrayList<IFullScreenObject>> {

    protected int perPage;
    protected int page;
    protected int userId;
    protected PhotoUploadingManager photoUploadingManager;

    public GetUserPhotosQuery(PhotoUploadingManager uploadingManager, int userId, int perPage, int page) {
        super((Class<ArrayList<IFullScreenObject>>) new ArrayList<IFullScreenObject>().getClass());
        this.photoUploadingManager = uploadingManager;
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

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_load_my_images;
    }
}

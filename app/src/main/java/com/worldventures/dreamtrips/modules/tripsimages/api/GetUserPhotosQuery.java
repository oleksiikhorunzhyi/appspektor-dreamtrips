package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManager;
import com.worldventures.dreamtrips.core.api.UploadPurpose;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import java.util.ArrayList;
import java.util.List;

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
        if (page == 1) result.addAll(getUploadTasks());
        result.addAll(getService().getUserPhotos(userId, perPage, page));
        return result;
    }

    private List<UploadTask> getUploadTasks() {
        return Queryable.from(photoUploadingManager.getUploadTasks(UploadPurpose.TRIP_IMAGE)).sortReverse().toList();
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_load_my_images;
    }
}

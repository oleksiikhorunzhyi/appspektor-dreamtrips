package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GetMembersPhotosQuery extends Query<ArrayList<IFullScreenObject>> {

    @Inject
    protected SnappyRepository db;

    protected int perPage;
    protected int page;

    public GetMembersPhotosQuery(int perPage, int page) {
        super((Class<ArrayList<IFullScreenObject>>) new ArrayList<IFullScreenObject>().getClass());
        this.perPage = perPage;
        this.page = page;
    }

    @Override
    public ArrayList<IFullScreenObject> loadDataFromNetwork() throws Exception {
        ArrayList<IFullScreenObject> result = new ArrayList<>();
        if (page == 1) result.addAll(getUploadTasks());
        result.addAll(getService().getMembersPhotos(perPage, page));
        return result;
    }

    private List<UploadTask> getUploadTasks() {
        return Queryable.from(db.getAllUploadTask())
                .filter(item -> item.getModule() != null &&
                        item.getModule().equals(UploadTask.Module.IMAGES))
                .sortReverse()
                .toList();
    }
}

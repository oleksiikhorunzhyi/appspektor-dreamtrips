package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.AmazonDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GetMyPhotosQuery extends Query<ArrayList<IFullScreenObject>> {

    @Inject
    protected SnappyRepository db;

    @Inject
    protected AmazonDelegate amazonDelegate;

    protected int perPage;
    protected int page;
    protected int currentUserId;

    public GetMyPhotosQuery(int currentUserId, int perPage, int page) {
        super((Class<ArrayList<IFullScreenObject>>) new ArrayList<IFullScreenObject>().getClass());
        this.currentUserId = currentUserId;
        this.perPage = perPage;
        this.page = page;
    }

    @Override
    public ArrayList<IFullScreenObject> loadDataFromNetwork() throws Exception {
        ArrayList<IFullScreenObject> result = new ArrayList<>();
        if (page == 1) result.addAll(getUploadTasks());
        result.addAll(getService().getMyPhotos(currentUserId, perPage, page));
        return result;
    }

    private List<ImageUploadTask> getUploadTasks() {
        List<ImageUploadTask> list = db.getAllImageUploadTask();
        Collections.reverse(list);
        return list;
    }
}

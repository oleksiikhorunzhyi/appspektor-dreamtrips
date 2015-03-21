package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GetMyPhotosQuery extends Query<ArrayList<IFullScreenAvailableObject>> {

    @Inject
    SnappyRepository db;
    int perPage;
    int page;
    private int currentUserId;

    public GetMyPhotosQuery(int currentUserId, int perPage, int page) {
        super((Class<ArrayList<IFullScreenAvailableObject>>) new ArrayList<IFullScreenAvailableObject>().getClass());
        this.currentUserId = currentUserId;
        this.perPage = perPage;
        this.page = page;
    }

    @Override
    public ArrayList<IFullScreenAvailableObject> loadDataFromNetwork() throws Exception {
        ArrayList<Photo> myPhotos = getService().getMyPhotos(currentUserId, perPage, page);
        List<ImageUploadTask> uploadTasks = getUploadTasks();
        ArrayList<IFullScreenAvailableObject> result = new ArrayList<>();
        result.addAll(uploadTasks);
        result.addAll(myPhotos);
        return result;
    }

    private List<ImageUploadTask> getUploadTasks() {
        List<ImageUploadTask> list = db.getAllImageUploadTask();
        Collections.reverse(list);
        return list;
    }
}

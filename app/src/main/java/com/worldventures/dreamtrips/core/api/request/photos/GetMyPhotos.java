package com.worldventures.dreamtrips.core.api.request.photos;

import com.worldventures.dreamtrips.core.api.request.base.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.uploader.ImageUploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GetMyPhotos extends DreamTripsRequest<ArrayList<IFullScreenAvailableObject>> {

    @Inject
    SnappyRepository db;
    int perPage;
    int page;
    private int currentUserId;

    public GetMyPhotos(int currentUserId, int perPage, int page) {
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

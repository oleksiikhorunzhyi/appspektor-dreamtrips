package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GetMyPhotosQuery extends GetMyPhotosBaseQuery {

    @Inject
    protected SnappyRepository db;

    public GetMyPhotosQuery(int currentUserId, int perPage, int page) {
        super(currentUserId, perPage, page);
    }

    @Override
    public ArrayList<IFullScreenAvailableObject> loadDataFromNetwork() throws Exception {
        List<ImageUploadTask> uploadTasks = getUploadTasks();
        ArrayList<IFullScreenAvailableObject> result = new ArrayList<>();
        result.addAll(uploadTasks);
        result.addAll(super.loadDataFromNetwork());
        return result;
    }

    private List<ImageUploadTask> getUploadTasks() {
        List<ImageUploadTask> list = db.getAllImageUploadTask();
        Collections.reverse(list);
        return list;
    }
}

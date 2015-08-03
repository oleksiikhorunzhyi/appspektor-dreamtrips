package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.AmazonDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.Collection;
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
        result.addAll(loadFromApi());
        return result;
    }

    private Collection<? extends IFullScreenObject> loadFromApi() {
        ArrayList<Photo> myPhotos = getService().getMyPhotos(currentUserId, perPage, page);
        ArrayList<IFullScreenObject> result = new ArrayList<>();
        result.addAll(myPhotos);
        return result;
    }

    private List<ImageUploadTask> getUploadTasks() {
        List<ImageUploadTask> list = db.getAllImageUploadTask();
        Collections.reverse(list);
        return Queryable.from(list)
                .sortReverse()
                .filter(task -> amazonDelegate.getTransferById(task.getAmazonTaskId()) != null)
                .toList();
    }
}

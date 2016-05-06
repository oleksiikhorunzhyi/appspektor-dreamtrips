package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;

public class UploadPhotosCommand extends Command<ArrayList<Photo>> {

    private CreatePhotoEntity createPhotoEntity;

    public UploadPhotosCommand(CreatePhotoEntity createPhotoEntity) {
        super((Class<ArrayList<Photo>>) new ArrayList<Photo>().getClass());
        this.createPhotoEntity = createPhotoEntity;
    }

    @Override
    public ArrayList<Photo> loadDataFromNetwork() throws Exception {
        return getService().uploadPhotos(createPhotoEntity);
    }
}

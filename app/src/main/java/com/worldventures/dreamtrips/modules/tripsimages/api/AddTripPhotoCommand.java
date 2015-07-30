package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class AddTripPhotoCommand extends DreamTripsRequest<Photo> {

    private ImageUploadTask uploadTask;

    public AddTripPhotoCommand(ImageUploadTask uploadTask) {
        super(Photo.class);
        this.uploadTask = uploadTask;
    }

    @Override
    public Photo loadDataFromNetwork() throws Exception {
        return getService().uploadTripPhoto(uploadTask);
    }
}

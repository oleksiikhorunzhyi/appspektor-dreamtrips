package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class AddTripPhotoCommand extends DreamTripsRequest<Photo> {

    private ImageUploadTask uploadTask;
    private UploadTask uploadTask2;

    public AddTripPhotoCommand(ImageUploadTask uploadTask) {
        super(Photo.class);
        this.uploadTask = uploadTask;
    }

    public AddTripPhotoCommand(UploadTask uploadTask2) {
        super(Photo.class);
        this.uploadTask2 = uploadTask2;
    }

    @Override
    public Photo loadDataFromNetwork() throws Exception {
        return getService().uploadTripPhoto(uploadTask2);
    }
}

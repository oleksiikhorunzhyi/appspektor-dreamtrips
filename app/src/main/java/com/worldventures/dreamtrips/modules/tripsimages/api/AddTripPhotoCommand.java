package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class AddTripPhotoCommand extends DreamTripsRequest<Photo> {

    private UploadTask uploadTask2;


    public AddTripPhotoCommand(UploadTask uploadTask2) {
        super(Photo.class);
        this.uploadTask2 = uploadTask2;
    }

    @Override
    public Photo loadDataFromNetwork() throws Exception {
        return getService().uploadTripPhoto(uploadTask2);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_create_image;
    }
}


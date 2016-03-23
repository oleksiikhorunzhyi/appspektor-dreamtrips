package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class EditTripPhotoCommand extends DreamTripsRequest<Photo> {

    private String uid;
    private UploadTask uploadTask;

    public EditTripPhotoCommand(String uid, UploadTask uploadTask) {
        super(Photo.class);
        this.uid = uid;
        this.uploadTask = uploadTask;
    }

    @Override
    public Photo loadDataFromNetwork() throws Exception {
        return getService().editTripPhoto(uid, uploadTask);
    }


    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_edit_image;
    }
}

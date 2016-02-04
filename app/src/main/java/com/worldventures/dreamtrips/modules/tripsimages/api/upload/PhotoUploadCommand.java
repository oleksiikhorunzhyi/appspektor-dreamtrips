package com.worldventures.dreamtrips.modules.tripsimages.api.upload;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.bucketlist.model.PhotoUploadResponse;

import retrofit.mime.TypedFile;

public class PhotoUploadCommand extends Command<PhotoUploadResponse> {


    private TypedFile typedFile;

    public PhotoUploadCommand(TypedFile typedFile) {
        super(PhotoUploadResponse.class);
        this.typedFile = typedFile;
    }

    @Override
    public PhotoUploadResponse loadDataFromNetwork() throws Exception {
        return getService().uploadPhoto(typedFile);
    }
}

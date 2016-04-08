package com.worldventures.dreamtrips.modules.tripsimages.api.upload;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.worldventures.dreamtrips.core.api.UploaderyApi;
import com.worldventures.dreamtrips.modules.bucketlist.model.PhotoUploadResponse;

import retrofit.mime.TypedFile;

public class PhotoUploadCommand extends RetrofitSpiceRequest<PhotoUploadResponse, UploaderyApi> {


    private TypedFile typedFile;

    public PhotoUploadCommand(TypedFile typedFile) {
        super(PhotoUploadResponse.class, UploaderyApi.class);
        this.typedFile = typedFile;
    }

    @Override
    public PhotoUploadResponse loadDataFromNetwork() throws Exception {
        return getService().uploadPhoto(typedFile);
    }

}

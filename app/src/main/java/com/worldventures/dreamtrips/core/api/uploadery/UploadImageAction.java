package com.worldventures.dreamtrips.core.api.uploadery;

import com.worldventures.dreamtrips.core.api.action.BaseHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.PhotoUploadResponse;

import java.io.File;

import io.techery.janet.body.FileBody;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Part;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Url;

@HttpAction(type = HttpAction.Type.MULTIPART, method = HttpAction.Method.POST)
public class UploadImageAction extends BaseHttpAction {

    @Url String uploaderyURL;

    @Part(value = "photo") FileBody fileBody;
    @Response PhotoUploadResponse photoUploadResponse;

    public UploadImageAction(String uploaderyURL, File imageFile) {
        this.uploaderyURL = uploaderyURL + "/upload";
        fileBody = new FileBody("image/*", imageFile);
    }

    public PhotoUploadResponse getPhotoUploadResponse() {
        return photoUploadResponse;
    }
}

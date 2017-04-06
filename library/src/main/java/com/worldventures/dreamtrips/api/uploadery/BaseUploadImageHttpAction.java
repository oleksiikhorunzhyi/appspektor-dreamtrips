package com.worldventures.dreamtrips.api.uploadery;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.uploadery.model.UploaderyImageResponse;

import java.io.File;
import java.io.IOException;

import io.techery.janet.body.FileBody;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Part;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Url;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;
import static io.techery.janet.http.annotations.HttpAction.Type.MULTIPART;

public abstract class BaseUploadImageHttpAction extends AuthorizedHttpAction {

    @Url
    public final String uploaderyURL;

    @Part(value = "photo")
    final FileBody fileBody;

    @Response
    UploaderyImageResponse uploaderyImageResponse;

    public BaseUploadImageHttpAction(String uploaderyURL, File imageFile) throws IOException {
        this.uploaderyURL = uploaderyURL + getUploaderyEndpoint();
        fileBody = new FileBody("image/*", imageFile);
    }

    public UploaderyImageResponse response() {
        return uploaderyImageResponse;
    }

    protected abstract String getUploaderyEndpoint();
}

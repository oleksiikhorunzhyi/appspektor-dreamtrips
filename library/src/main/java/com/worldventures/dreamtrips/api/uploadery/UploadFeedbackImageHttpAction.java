package com.worldventures.dreamtrips.api.uploadery;

import java.io.File;
import java.io.IOException;

import io.techery.janet.http.annotations.HttpAction;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;
import static io.techery.janet.http.annotations.HttpAction.Type.MULTIPART;

@HttpAction(type = MULTIPART, method = POST)
public class UploadFeedbackImageHttpAction extends BaseUploadImageHttpAction {

    private static final String UPLOAD_URL_SUFFIX = "/upload/feedback";

    public UploadFeedbackImageHttpAction(String uploaderyURL, File imageFile) throws IOException {
        super(uploaderyURL, imageFile);
    }

    protected String getUploaderyEndpoint() {
        return UPLOAD_URL_SUFFIX;
    }
}

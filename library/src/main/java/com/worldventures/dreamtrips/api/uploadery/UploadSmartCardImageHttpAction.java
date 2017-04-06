package com.worldventures.dreamtrips.api.uploadery;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.uploadery.model.UploaderyImageResponse;

import java.io.File;
import java.io.IOException;

import io.techery.janet.body.FileBody;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Part;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Url;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;
import static io.techery.janet.http.annotations.HttpAction.Type.MULTIPART;

@HttpAction(type = MULTIPART, method = POST)
public class UploadSmartCardImageHttpAction extends AuthorizedHttpAction {

    private static final String URL = "%s/upload/users/{user_id}/smartcard/{sc_id}/photo";

    @Url String uploaderyUrl;

    @Path("user_id") String userId;

    @Path("sc_id") String smartcardId;

    @Part(value = "photo")
    final FileBody fileBody;

    @Response
    UploaderyImageResponse uploaderyImageResponse;

    public UploadSmartCardImageHttpAction(String baseUrl, String userId, String smartcardId, File imageFile) throws IOException {
        this.userId = userId;
        this.smartcardId = smartcardId;
        uploaderyUrl = String.format(URL, baseUrl);
        fileBody = new FileBody("image/*", imageFile);
    }

    public UploaderyImageResponse response() {
        return uploaderyImageResponse;
    }

}

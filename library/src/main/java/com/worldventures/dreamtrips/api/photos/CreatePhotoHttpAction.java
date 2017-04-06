package com.worldventures.dreamtrips.api.photos;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.photos.model.PhotoCreationParams;
import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "/api/photos", method = POST)
public class CreatePhotoHttpAction extends AuthorizedHttpAction {

    @Body
    public final PhotoCreationParams params;

    @Response
    PhotoSimple photo;

    public CreatePhotoHttpAction(PhotoCreationParams params) {
        this.params = params;
    }

    public PhotoSimple response() {
        return photo;
    }
}

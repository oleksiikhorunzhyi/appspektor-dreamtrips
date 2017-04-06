package com.worldventures.dreamtrips.api.photos;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;
import com.worldventures.dreamtrips.api.photos.model.PhotoUpdateParams;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.PUT;

@HttpAction(value = "/api/photos/{uid}", method = PUT)
public class UpdatePhotoHttpAction extends AuthorizedHttpAction {

    @Path("uid")
    public final String uid;

    @Body
    public final PhotoUpdateParams params;

    @Response
    PhotoSimple photo;

    public UpdatePhotoHttpAction(String uid, PhotoUpdateParams params) {
        this.uid = uid;
        this.params = params;
    }

    public PhotoSimple response() {
        return photo;
    }
}

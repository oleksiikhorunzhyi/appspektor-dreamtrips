package com.worldventures.dreamtrips.api.photos;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;
import com.worldventures.dreamtrips.api.photos.model.PhotosCreationParams;

import java.util.List;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "/api/photos", method = POST)
public class CreatePhotosHttpAction extends AuthorizedHttpAction {

    @Body
    public final PhotosCreationParams params;

    @Response
    List<PhotoSimple> photos;

    public CreatePhotosHttpAction(PhotosCreationParams params) {
        this.params = params;
    }

    public List<PhotoSimple> response() {
        return photos;
    }
}

package com.worldventures.dreamtrips.api.photos;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/users/{user_id}/photos")
public class GetPhotosOfUserHttpAction extends PaginatedHttpAction {

    @Path("user_id")
    public final int userId;

    @Response
    List<PhotoSimple> photos;

    public GetPhotosOfUserHttpAction(int userId, int page, int perPage) {
        super(page, perPage);
        this.userId = userId;
    }

    public List<PhotoSimple> response() {
        return photos;
    }
}

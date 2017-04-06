package com.worldventures.dreamtrips.api.photos;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/photos")
public class GetPhotosHttpAction extends PaginatedHttpAction {

    @Response
    List<PhotoSimple> photos;

    public GetPhotosHttpAction(int page, int perPage) {
        super(page, perPage);
    }

    public List<PhotoSimple> response() {
        return photos;
    }
}

package com.worldventures.dreamtrips.api.ysbh;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.ysbh.model.YSBHPhoto;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/ysbh_photos")
public class GetYSBHPhotosHttpAction extends PaginatedHttpAction {

    @Response
    List<YSBHPhoto> photos;

    public GetYSBHPhotosHttpAction(int page, int perPage) {
        super(page, perPage);
    }

    public List<YSBHPhoto> response() {
        return photos;
    }
}

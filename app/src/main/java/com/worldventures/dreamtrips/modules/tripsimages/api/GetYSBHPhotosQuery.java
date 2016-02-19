package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.YSBHPhoto;

import java.util.ArrayList;

public class GetYSBHPhotosQuery extends Query<ArrayList<IFullScreenObject>> {
    protected int perPage;
    protected int page;

    public GetYSBHPhotosQuery(int perPage, int page) {
        super((Class<ArrayList<IFullScreenObject>>) new ArrayList<IFullScreenObject>().getClass());
        this.perPage = perPage;
        this.page = page;
    }

    @Override
    public ArrayList<IFullScreenObject> loadDataFromNetwork() throws Exception {
        ArrayList<YSBHPhoto> photos = getService().getYouShouldBeHerePhotos(perPage, page);
        ArrayList<IFullScreenObject> result = new ArrayList<>();
        result.addAll(photos);
        return result;
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_load_ysbh_images;
    }
}

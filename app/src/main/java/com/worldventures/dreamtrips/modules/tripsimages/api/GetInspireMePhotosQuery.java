package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;

public class GetInspireMePhotosQuery extends Query<ArrayList<IFullScreenObject>> {

    protected int perPage;
    protected int page;
    private double randomSeed;

    public GetInspireMePhotosQuery(int perPage, int page, double randomSeed) {
        super((Class<ArrayList<IFullScreenObject>>) new ArrayList<IFullScreenObject>().getClass());
        this.perPage = perPage;
        this.page = page;
        this.randomSeed = randomSeed;
    }

    @Override
    public ArrayList<IFullScreenObject> loadDataFromNetwork() throws Exception {
        ArrayList<Inspiration> inspirations = getService().getInspirationsPhotos(perPage, page,randomSeed);
        ArrayList<IFullScreenObject> result = new ArrayList<>();
        result.addAll(inspirations);
        return result;
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_load_inspire_images;
    }
}

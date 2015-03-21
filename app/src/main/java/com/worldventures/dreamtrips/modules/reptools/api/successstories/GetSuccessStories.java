package com.worldventures.dreamtrips.modules.reptools.api.successstories;

import com.worldventures.dreamtrips.core.api.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;

import java.util.ArrayList;

public class GetSuccessStories extends DreamTripsRequest<ArrayList<SuccessStory>> {

    public GetSuccessStories() {
        super((Class<ArrayList<SuccessStory>>) new ArrayList<SuccessStory>().getClass());
    }

    @Override
    public ArrayList<SuccessStory> loadDataFromNetwork() throws Exception {
        ArrayList<SuccessStory> successStores = getService().getSuccessStores();

       /* ArrayList<SuccessStory> successStores = new ArrayList<>();
        successStores.add(new SuccessStory());
        successStores.add(new SuccessStory());
        successStores.add(new SuccessStory());
        successStores.add(new SuccessStory());
        successStores.add(new SuccessStory());
        successStores.add(new SuccessStory());*/
        return successStores;
    }
}

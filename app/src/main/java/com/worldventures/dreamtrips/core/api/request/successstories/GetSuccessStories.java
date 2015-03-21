package com.worldventures.dreamtrips.core.api.request.successstories;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.SuccessStory;

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

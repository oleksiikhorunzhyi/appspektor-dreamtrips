package com.worldventures.dreamtrips.modules.reptools.api.successstories;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;

import java.util.ArrayList;

public class GetSuccessStoriesQuery extends Query<ArrayList<SuccessStory>> {

    public GetSuccessStoriesQuery() {
        super((Class<ArrayList<SuccessStory>>) new ArrayList<SuccessStory>().getClass());
    }

    @Override
    public ArrayList<SuccessStory> loadDataFromNetwork() throws Exception {
        ArrayList<SuccessStory> successStores = getService().getSuccessStores();
        return successStores;
    }
}

package com.worldventures.dreamtrips.modules.infopages.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;

import java.util.ArrayList;

public class GetFeedbackTypesQuery extends Query<ArrayList<FeedbackType>> {

    public GetFeedbackTypesQuery() {
        super((Class<ArrayList<FeedbackType>>) new ArrayList<FeedbackType>().getClass());
    }

    @Override
    public ArrayList<FeedbackType> loadDataFromNetwork() throws Exception {
        return getService().getFeedbackReasons();
    }

}

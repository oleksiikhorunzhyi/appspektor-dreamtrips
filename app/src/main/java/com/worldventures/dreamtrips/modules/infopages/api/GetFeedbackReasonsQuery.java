package com.worldventures.dreamtrips.modules.infopages.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;

import java.util.ArrayList;

public class GetFeedbackReasonsQuery extends Query<ArrayList<FeedbackType>> {

   public GetFeedbackReasonsQuery() {
      super((Class<ArrayList<FeedbackType>>) new ArrayList<FeedbackType>().getClass());
   }

   @Override
   public ArrayList<FeedbackType> loadDataFromNetwork() throws Exception {
      return getService().getFeedbackReasons();
   }

   @Override
   public int getErrorMessage() {
      return R.string.error_failed_to_load_feedback_reasons;
   }
}

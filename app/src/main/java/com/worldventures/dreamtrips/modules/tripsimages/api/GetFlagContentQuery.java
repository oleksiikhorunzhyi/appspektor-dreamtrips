package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.ArrayList;

public class GetFlagContentQuery extends Query<ArrayList<Flag>> {

   public GetFlagContentQuery() {
      super((Class<ArrayList<Flag>>) new ArrayList<Flag>().getClass());
   }

   @Override
   public ArrayList<Flag> loadDataFromNetwork() throws Exception {
      return getService().getFlags();
   }

   @Override
   public int getErrorMessage() {
      return R.string.error_fail_to_load_flag_reason;
   }

}

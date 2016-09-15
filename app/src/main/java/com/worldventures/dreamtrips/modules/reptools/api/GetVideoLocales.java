package com.worldventures.dreamtrips.modules.reptools.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;

import java.util.ArrayList;

public class GetVideoLocales extends Query<ArrayList<VideoLocale>> {

   public GetVideoLocales() {
      super((Class<ArrayList<VideoLocale>>) new ArrayList<VideoLocale>().getClass());
   }

   @Override
   public ArrayList<VideoLocale> loadDataFromNetwork() throws Exception {
      return getService().getTrainingVideosLocales();
   }

   @Override
   public int getErrorMessage() {
      return R.string.error_fail_to_locales;
   }
}
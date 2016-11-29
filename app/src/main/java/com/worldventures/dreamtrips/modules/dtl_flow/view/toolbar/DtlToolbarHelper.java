package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import android.content.res.Resources;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

public class DtlToolbarHelper {

   public static String provideLocationCaption(Resources resources, DtlLocation dtlLocation) {
      if (dtlLocation == null) return "";

      switch (dtlLocation.locationSourceType()) {
         case NEAR_ME:
         case EXTERNAL:
            return dtlLocation.longName();
         case FROM_MAP:
            return TextUtils.isEmpty(dtlLocation.longName()) ? resources.getString(R.string.dtl_nearby_caption_empty) : resources
                  .getString(R.string.dtl_nearby_caption_format, dtlLocation.longName());
         default:
            return "";
      }
   }
}

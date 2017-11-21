package com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;

@Layout(R.layout.screen_dtl_location_change)
public class DtlLocationChangePath extends DtlMasterPath {

   private String prefilledMerchantSearchQuery;

   public DtlLocationChangePath(String prefilledMerchantSearchQuery) {
      this.prefilledMerchantSearchQuery = prefilledMerchantSearchQuery;
   }

   public DtlLocationChangePath() {
      // This constructor is intentionally empty. Nothing special is needed here.
   }

   @Override
   public PathAttrs getAttrs() {
      return PathAttrs.WITHOUT_DRAWER;
   }

   @Override
   public boolean isFullScreen() {
      return false;
   }

   public String getPrefilledMerchantSearchQuery() {
      return prefilledMerchantSearchQuery;
   }
}

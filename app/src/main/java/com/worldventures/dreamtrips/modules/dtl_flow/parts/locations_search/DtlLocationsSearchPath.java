package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;

@Layout(R.layout.screen_dtl_locations_search)
public class DtlLocationsSearchPath extends DtlMasterPath {

   @Override
   public PathAttrs getAttrs() {
      return PathAttrs.WITH_DRAWER;
   }


   @Override
   public boolean isFullScreen() {
      return true;
   }
}

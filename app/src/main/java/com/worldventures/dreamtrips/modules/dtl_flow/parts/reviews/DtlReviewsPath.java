package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;
import flow.path.Path;

/**
 * Created by yair.carreno on 2/1/2017.
 */

@Layout(R.layout.screen_dtl_reviews)
public class DtlReviewsPath extends DtlMasterPath {

   @Override
   public PathAttrs getAttrs() {
      return PathAttrs.WITHOUT_DRAWER;
   }

   @Override
   public Path getMasterToolbarPath() {
      return MasterToolbarPath.INSTANCE;
   }
}

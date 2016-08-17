package com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;

@Layout(R.layout.screen_dtl_fullscreen_image)
public class DtlFullscreenImagePath extends DtlMasterPath {

   private final String url;

   public DtlFullscreenImagePath(String url) {
      this.url = url;
   }

   public String getUrl() {
      return url;
   }

   @Override
   public boolean isFullScreen() {
      return true;
   }

   @Override
   public boolean shouldHideDrawer() {
      return true;
   }

   @Override
   public PathAttrs getAttrs() {
      return PathAttrs.WITHOUT_DRAWER;
   }
}

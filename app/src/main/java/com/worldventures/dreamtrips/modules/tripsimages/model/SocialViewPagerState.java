package com.worldventures.dreamtrips.modules.tripsimages.model;

import java.io.Serializable;

public class SocialViewPagerState implements Serializable {
   private boolean contentWrapperVisible = true;
   private boolean tagHolderVisible = true;

   public SocialViewPagerState() {
   }

   public void setContentWrapperVisible(boolean contentWrapperVisible) {
      this.contentWrapperVisible = contentWrapperVisible;
   }

   public boolean isTagHolderVisible() {
      return tagHolderVisible;
   }

   public void setTagHolderVisible(boolean tagHolderVisible) {
      this.tagHolderVisible = tagHolderVisible;
   }

   public boolean isContentWrapperVisible() {
      return contentWrapperVisible;
   }
}

package com.worldventures.dreamtrips.modules.tripsimages.model;

import java.io.Serializable;

public class SocialViewPagerState implements Serializable {
   private boolean contentWrapperVisible;
   private boolean tagHolderVisible;

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

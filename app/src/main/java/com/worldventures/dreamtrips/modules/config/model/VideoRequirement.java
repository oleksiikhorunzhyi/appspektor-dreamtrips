package com.worldventures.dreamtrips.modules.config.model;

public class VideoRequirement {

   public static final int DEFAULT_MAX_VIDEO_DURATION_SEC = 90;

   private int videoMaxLength;

   public int getVideoMaxLength() {
      return videoMaxLength == 0 ? DEFAULT_MAX_VIDEO_DURATION_SEC : videoMaxLength;
   }

   public void setVideoMaxLength(int videoMaxLength) {
      this.videoMaxLength = videoMaxLength;
   }
}

package com.worldventures.dreamtrips.modules.feed.model.video;

import com.worldventures.dreamtrips.modules.feed.model.BaseFeedEntity;

public class Video extends BaseFeedEntity {

   private String title;
   private String thumbnail;
   private String sdUrl;
   private String hdUrl;
   private double aspectRatio;
   private long duration;

   public Video() {
   }

   public String getTitle() {
      return title;
   }

   public String getThumbnail() {
      return thumbnail;
   }

   public String getSdUrl() {
      return sdUrl;
   }

   public String getHdUrl() {
      return hdUrl;
   }

   public long getDuration() {
      return duration;
   }

   public double getAspectRatio() {
      return aspectRatio;
   }

   @Override
   public String place() {
      return null;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void setThumbnail(String thumbnail) {
      this.thumbnail = thumbnail;
   }

   public void setSdUrl(String sdUrl) {
      this.sdUrl = sdUrl;
   }

   public void setHdUrl(String hdUrl) {
      this.hdUrl = hdUrl;
   }

   public void setDuration(long duration) {
      this.duration = duration;
   }

   public void setAspectRatio(double aspectRatio) {
      this.aspectRatio = aspectRatio;
   }
}

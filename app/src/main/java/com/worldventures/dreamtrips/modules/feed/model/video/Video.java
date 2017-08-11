package com.worldventures.dreamtrips.modules.feed.model.video;

import com.worldventures.dreamtrips.modules.feed.model.BaseFeedEntity;

public class Video extends BaseFeedEntity {

   private String uploadId;
   private String thumbnail;
   private String sdUrl;
   private String hdUrl;
   private double aspectRatio;

   public Video() {
   }

   public String getUploadId() {
      return uploadId;
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

   public double getAspectRatio() {
      return aspectRatio;
   }

   @Override
   public String place() {
      return null;
   }

   public void setUploadId(String uploadId) {
      this.uploadId = uploadId;
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

   public void setAspectRatio(double aspectRatio) {
      this.aspectRatio = aspectRatio;
   }
}

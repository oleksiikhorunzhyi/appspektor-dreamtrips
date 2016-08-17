package com.worldventures.dreamtrips.modules.video.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class Video {
   public static final String FEATURED = "Featured";

   @SerializedName("image_url") private String imageUrl;
   @SerializedName("video_url") private String mp4Url;
   private String name;
   private String category;
   private String duration;

   private transient CachedEntity entity;

   public Video() {
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public String getCategory() {
      return category;
   }

   public String getImageUrl() {
      return imageUrl;
   }

   public String getMp4Url() {
      return mp4Url;
   }

   public String getUid() {
      return mp4Url;
   }

   public String getVideoName() {
      return name;
   }

   public String getDuration() {
      return duration;
   }

   public CachedEntity getCacheEntity() {
      if (entity == null) {
         entity = new CachedEntity(this.getMp4Url(), this.getUid(), this.getVideoName());
      }
      return entity;
   }

   public void setCacheEntity(CachedEntity entity) {
      this.entity = entity;
   }

   public boolean isFeatured() {
      return !TextUtils.isEmpty(category) && category.trim().equals(FEATURED);
   }

   public boolean isRecent() {
      return !isFeatured();
   }

}

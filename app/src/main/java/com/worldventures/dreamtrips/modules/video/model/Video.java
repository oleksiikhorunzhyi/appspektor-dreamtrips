package com.worldventures.dreamtrips.modules.video.model;

import android.text.TextUtils;

public class Video {
   private static final String FEATURED = "Featured";

   private String imageUrl;
   private String videoUrl;
   private String name;
   private String category;
   private String duration;

   private transient CachedEntity entity;

   public Video(String imageUrl, String videoUrl, String name, String category, String duration) {
      this.imageUrl = imageUrl;
      this.videoUrl = videoUrl;
      this.name = name;
      this.category = category;
      this.duration = duration;
   }

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

   public String getVideoUrl() {
      return videoUrl;
   }

   public String getUid() {
      return videoUrl;
   }

   public String getVideoName() {
      return name;
   }

   public String getDuration() {
      return duration;
   }

   public CachedEntity getCacheEntity() {
      if (entity == null) {
         entity = new CachedEntity(this.getVideoUrl(), this.getUid(), this.getVideoName());
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

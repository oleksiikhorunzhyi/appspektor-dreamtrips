package com.worldventures.dreamtrips.modules.video.model;

import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;

public class Video {
   private static final String FEATURED = "Featured";

   private String imageUrl;
   private String videoUrl;
   private String name;
   private String category;
   private String duration;
   private String language;

   private transient CachedModel entity;

   public Video(String imageUrl, String videoUrl, String name, String category, String duration, String language) {
      this.imageUrl = imageUrl;
      this.videoUrl = videoUrl;
      this.name = name;
      this.category = category;
      this.duration = duration;
      this.language = language;
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

   public String getLanguage() {
      return language;
   }

   public CachedModel getCacheEntity() {
      if (entity == null) {
         entity = new CachedModel(getVideoUrl(), getUid(), getVideoName());
         entity.setEntityClass(Video.class);
      }
      return entity;
   }

   public void setCacheEntity(CachedModel entity) {
      this.entity = entity;
   }

   public boolean isFeatured() {
      return !TextUtils.isEmpty(category) && category.trim().equals(FEATURED);
   }

   public boolean isRecent() {
      return !isFeatured();
   }

}

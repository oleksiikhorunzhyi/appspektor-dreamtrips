package com.worldventures.dreamtrips.modules.membership.model;

import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.util.Date;

public class Podcast {

   private String title;
   private String category;
   private String description;
   private Date date;
   private long size;
   private long duration;
   private String imageUrl;
   private String fileUrl;
   private String speaker;

   private transient CachedEntity entity;

   public Podcast() {
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getTitle() {
      return title;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public String getCategory() {
      return category;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getDescription() {
      return description;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public Date getDate() {
      return date;
   }

   public void setSize(long size) {
      this.size = size;
   }

   public long getSize() {
      return size;
   }

   public void setDuration(long duration) {
      this.duration = duration;
   }

   public long getDuration() {
      return duration;
   }

   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   public String getImageUrl() {
      return imageUrl;
   }

   public void setFileUrl(String fileUrl) {
      this.fileUrl = fileUrl;
   }

   public String getFileUrl() {
      return fileUrl;
   }

   public String getUid() {
      return fileUrl;
   }

   public void setSpeaker(String speaker) {
      this.speaker = speaker;
   }

   public String getSpeaker() {
      return speaker;
   }

   public CachedEntity getCacheEntity() {
      if (entity == null) {
         entity = new CachedEntity(getFileUrl(), getUid(), getTitle());
      }
      return entity;
   }

   public void setCacheEntity(CachedEntity entity) {
      this.entity = entity;
   }

   @Override
   public String toString() {
      return "Podcast{" +
            "title='" + title + '\'' +
            ", category='" + category + '\'' +
            ", description='" + description + '\'' +
            ", date=" + date +
            ", size=" + size +
            ", duration=" + duration +
            ", imageUrl='" + imageUrl + '\'' +
            ", fileUrl='" + fileUrl + '\'' +
            ", speaker=" + speaker +
            '}';
   }
}

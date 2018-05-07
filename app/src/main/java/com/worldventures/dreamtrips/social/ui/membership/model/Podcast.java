package com.worldventures.dreamtrips.social.ui.membership.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.core.model.CachedModel;

import java.io.Serializable;
import java.util.Date;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Podcast implements Serializable {

   private String title;
   private String category;
   private String description;
   private Date date;
   private int duration;
   private String imageUrl;
   private String fileUrl;
   private CachedModel cachedModel;

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getCategory() {
      return category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public int getDuration() {
      return duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public String getImageUrl() {
      return imageUrl;
   }

   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   public String getFileUrl() {
      return fileUrl;
   }

   public void setFileUrl(String fileUrl) {
      this.fileUrl = fileUrl;
   }

   public CachedModel getCachedModel() {
      return cachedModel;
   }

   public void setCachedModel(CachedModel cachedModel) {
      this.cachedModel = cachedModel;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      Podcast podcast = (Podcast) o;
      return fileUrl.equals(podcast.fileUrl);
   }

   @Override
   public int hashCode() {
      return fileUrl.hashCode();
   }
}


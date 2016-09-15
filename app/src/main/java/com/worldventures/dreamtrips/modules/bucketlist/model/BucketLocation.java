package com.worldventures.dreamtrips.modules.bucketlist.model;


import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BucketLocation implements Serializable {

   private String name;
   private String url;
   private String description;
   @SerializedName("shortDescription") private String shortDescription;
   private boolean liked;
   @SerializedName("likes_count") private int likesCount;

   public BucketLocation() {
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getShort_description() {
      return shortDescription;
   }

   public void setShort_description(String shortDescription) {
      this.shortDescription = shortDescription;
   }

   public boolean isLiked() {
      return liked;
   }

   public void setLiked(boolean liked) {
      this.liked = liked;
   }

   public int getLikesCount() {
      return likesCount;
   }

   public void setLikesCount(int likesCount) {
      this.likesCount = likesCount;
   }
}

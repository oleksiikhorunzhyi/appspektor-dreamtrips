package com.worldventures.dreamtrips.modules.bucketlist.model;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

public class PopularBucketItem extends BaseEntity implements Filterable {

   private String name;
   private String description;
   private String coverPhotoUrl;

   private transient boolean loading = false;

   public PopularBucketItem() {
      super();
   }

   public void setCoverPhotoUrl(String coverPhotoUrl) {
      this.coverPhotoUrl = coverPhotoUrl;
   }

   public String getCoverPhotoUrl(int w, int h) {
      return coverPhotoUrl;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean isLoading() {
      return loading;
   }

   public void setLoading(boolean loading) {
      this.loading = loading;
   }

   @Override
   public boolean containsQuery(String query) {
      return query == null || name.toLowerCase().contains(query) || description.toLowerCase().contains(query);
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeString(this.name);
      dest.writeString(this.description);
      dest.writeString(this.coverPhotoUrl);
   }

   public PopularBucketItem(Parcel in) {
      super(in);
      this.name = in.readString();
      this.description = in.readString();
      this.coverPhotoUrl = in.readString();
   }

   public static final Creator<PopularBucketItem> CREATOR = new Creator<PopularBucketItem>() {
      @Override
      public PopularBucketItem createFromParcel(Parcel in) {
         return new PopularBucketItem(in);
      }

      @Override
      public PopularBucketItem[] newArray(int size) {
         return new PopularBucketItem[size];
      }
   };
}

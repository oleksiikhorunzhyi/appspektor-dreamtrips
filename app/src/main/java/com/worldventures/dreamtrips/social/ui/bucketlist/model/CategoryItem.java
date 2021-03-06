package com.worldventures.dreamtrips.social.ui.bucketlist.model;

import android.os.Parcel;

import com.worldventures.core.model.BaseEntity;

public class CategoryItem extends BaseEntity {

   private String name;

   public CategoryItem() {
      //do nothing
   }

   public CategoryItem(int id, String name) {
      setId(id);
      this.name = name;
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return name;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeString(this.name);
   }

   public CategoryItem(Parcel in) {
      super(in);
      this.name = in.readString();
   }

   public static final Creator<CategoryItem> CREATOR = new Creator<CategoryItem>() {
      @Override
      public CategoryItem createFromParcel(Parcel in) {
         return new CategoryItem(in);
      }

      @Override
      public CategoryItem[] newArray(int size) {
         return new CategoryItem[size];
      }
   };
}

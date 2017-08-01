package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcelable;

import java.util.Date;

public abstract class BaseMediaEntity implements Parcelable {
   protected TripImageType type;
   protected String uid;
   protected Date createdAt;

   public TripImageType getType() {
      return type;
   }

   public void setType(TripImageType type) {
      this.type = type;
   }

   public String getUid() {
      return uid;
   }

   public void setUid(String uid) {
      this.uid = uid;
   }

   public Date getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      BaseMediaEntity that = (BaseMediaEntity) o;

      return uid != null ? uid.equals(that.uid) : that.uid == null;
   }

   @Override
   public int hashCode() {
      return uid != null ? uid.hashCode() : 0;
   }
}

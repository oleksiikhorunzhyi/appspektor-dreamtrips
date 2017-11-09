package com.worldventures.dreamtrips.social.ui.tripsimages.model;

import android.os.Parcelable;

import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;

public abstract class BaseMediaEntity<T extends FeedEntity> implements Parcelable {
   protected TripImageType type;
   protected T item;

   public TripImageType getType() {
      return type;
   }

   public void setType(TripImageType type) {
      this.type = type;
   }

   public T getItem() {
      return item;
   }

   public void setItem(T item) {
      this.item = item;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      BaseMediaEntity that = (BaseMediaEntity) o;

      return item.getUid() != null ? item.getUid().equals(that.getItem().getUid()) : that.item.getUid() == null;
   }

   @Override
   public int hashCode() {
      return item.getUid() != null ? item.getUid().hashCode() : 0;
   }
}

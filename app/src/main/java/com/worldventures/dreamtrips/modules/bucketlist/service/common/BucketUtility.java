package com.worldventures.dreamtrips.modules.bucketlist.service.common;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public final class BucketUtility {
   public static Func1<BucketItem, Boolean> filterByType(BucketItem.BucketType type) {
      switch (type) {
         case LOCATION:
            return locationOnly();
         case DINING:
            return diningOnly();
         case ACTIVITY:
            return activityOnly();
         default:
            throw new AssertionError("Undefined bucket type");
      }
   }

   public static Observable.Transformer<List<BucketItem>, List<BucketItem>> disJoinByType(BucketItem.BucketType type) {
      return new FilterTransformer(type);
   }

   public static Func1<BucketItem, Boolean> locationOnly() {
      return FilterBy.LOCATION;
   }

   public static Func1<BucketItem, Boolean> diningOnly() {
      return FilterBy.DINING;
   }

   public static Func1<BucketItem, Boolean> activityOnly() {
      return FilterBy.ACTIVITY;
   }

   public static BucketItem.BucketType typeFromItem(BucketItem item) {
      return BucketItem.BucketType.valueOf(item.getType().toUpperCase());
   }

   public static BucketItem findItemByPhoto(List<BucketItem> items, BucketPhoto photo) {
      BucketItem item = Queryable.from(items).firstOrDefault(bucketItem -> bucketItem.getPhotos().contains(photo));
      return item != null ? item : null;
   }

   private BucketUtility() {
      throw new AssertionError("No instance");
   }

   private enum FilterBy implements Func1<BucketItem, Boolean> {
      LOCATION(BucketItem.BucketType.LOCATION),
      DINING(BucketItem.BucketType.DINING),
      ACTIVITY(BucketItem.BucketType.ACTIVITY);

      private final BucketItem.BucketType type;

      FilterBy(BucketItem.BucketType type) {
         this.type = type;
      }

      @Override
      public Boolean call(BucketItem item) {
         return item.getType().equalsIgnoreCase(type.getName());
      }
   }

   private static final class FilterTransformer implements Observable.Transformer<List<BucketItem>, List<BucketItem>> {
      private BucketItem.BucketType type;

      public FilterTransformer(BucketItem.BucketType type) {
         this.type = type;
      }

      @Override
      public Observable<List<BucketItem>> call(Observable<List<BucketItem>> listObservable) {
         return listObservable.flatMap(bucketItems -> Observable.from(bucketItems).filter(filterByType(type)).toList());
      }
   }
}
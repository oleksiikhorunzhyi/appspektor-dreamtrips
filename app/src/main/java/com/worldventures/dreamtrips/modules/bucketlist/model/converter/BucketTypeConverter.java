package com.worldventures.dreamtrips.modules.bucketlist.model.converter;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketType;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class BucketTypeConverter implements Converter<BucketType, BucketItem.BucketType> {

   @Override
   public Class<BucketType> sourceClass() {
      return BucketType.class;
   }

   @Override
   public Class<BucketItem.BucketType> targetClass() {
      return BucketItem.BucketType.class;
   }

   @Override
   public BucketItem.BucketType convert(MapperyContext mapperyContext, BucketType bucketType) {
      return mapBucketItemType(bucketType);
   }

   private BucketItem.BucketType mapBucketItemType(BucketType type) {
      switch (type) {
         case ACTIVITY:
            return BucketItem.BucketType.ACTIVITY;
         case DINING:
            return BucketItem.BucketType.DINING;
         case LOCATION:
            return BucketItem.BucketType.LOCATION;
      }
      throw new IllegalArgumentException("No such type");
   }
}

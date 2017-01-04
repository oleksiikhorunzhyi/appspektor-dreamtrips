package com.worldventures.dreamtrips.modules.bucketlist.model.converter;


import com.worldventures.dreamtrips.api.bucketlist.model.BucketListLocation;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class PopularBucketItemFromLocationConverter implements Converter<BucketListLocation, PopularBucketItem> {

   @Override
   public Class<BucketListLocation> sourceClass() {
      return BucketListLocation.class;
   }

   @Override
   public Class<PopularBucketItem> targetClass() {
      return PopularBucketItem.class;
   }

   @Override
   public PopularBucketItem convert(MapperyContext mapperyContext, BucketListLocation bucketLocation) {
      PopularBucketItem popularBucketItem = new PopularBucketItem();
      popularBucketItem.setId(bucketLocation.id());
      popularBucketItem.setDescription(bucketLocation.description());
      popularBucketItem.setName(bucketLocation.name());
      popularBucketItem.setCoverPhotoUrl(bucketLocation.coverPhoto().url());
      return popularBucketItem;
   }
}

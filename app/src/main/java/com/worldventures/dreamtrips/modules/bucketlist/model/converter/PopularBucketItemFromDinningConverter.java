package com.worldventures.dreamtrips.modules.bucketlist.model.converter;


import com.worldventures.dreamtrips.api.bucketlist.model.BucketListDining;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class PopularBucketItemFromDinningConverter implements Converter<BucketListDining, PopularBucketItem> {

   @Override
   public Class<BucketListDining> sourceClass() {
      return BucketListDining.class;
   }

   @Override
   public Class<PopularBucketItem> targetClass() {
      return PopularBucketItem.class;
   }

   @Override
   public PopularBucketItem convert(MapperyContext mapperyContext, BucketListDining bucketListDining) {
      PopularBucketItem popularBucketItem = new PopularBucketItem();
      popularBucketItem.setId(bucketListDining.id());
      popularBucketItem.setDescription(bucketListDining.description());
      popularBucketItem.setName(bucketListDining.name());
      popularBucketItem.setCoverPhotoUrl(bucketListDining.coverPhoto().url());
      return popularBucketItem;
   }
}

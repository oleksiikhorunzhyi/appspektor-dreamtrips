package com.worldventures.dreamtrips.modules.bucketlist.model.converter;


import com.worldventures.dreamtrips.api.bucketlist.model.BucketListActivity;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class PopularBucketItemFromActivityConverter implements Converter<BucketListActivity, PopularBucketItem> {

   @Override
   public Class<BucketListActivity> sourceClass() {
      return BucketListActivity.class;
   }

   @Override
   public Class<PopularBucketItem> targetClass() {
      return PopularBucketItem.class;
   }

   @Override
   public PopularBucketItem convert(MapperyContext mapperyContext, BucketListActivity bucketListActivity) {
      PopularBucketItem popularBucketItem = new PopularBucketItem();
      popularBucketItem.setId(bucketListActivity.id());
      popularBucketItem.setDescription(bucketListActivity.description());
      popularBucketItem.setName(bucketListActivity.name());
      popularBucketItem.setCoverPhotoUrl(bucketListActivity.coverPhoto().url());
      return popularBucketItem;
   }
}

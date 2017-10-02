package com.worldventures.dreamtrips.social.ui.bucketlist.model.converter;


import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListActivity;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.PopularBucketItem;

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

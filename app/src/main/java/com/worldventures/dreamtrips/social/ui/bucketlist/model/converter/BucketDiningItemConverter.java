package com.worldventures.dreamtrips.social.ui.bucketlist.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListDining;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.DiningItem;

import io.techery.mappery.MapperyContext;

public class BucketDiningItemConverter implements Converter<BucketListDining, DiningItem> {

   @Override
   public Class<BucketListDining> sourceClass() {
      return BucketListDining.class;
   }

   @Override
   public Class<DiningItem> targetClass() {
      return DiningItem.class;
   }

   @Override
   public DiningItem convert(MapperyContext mapperyContext, BucketListDining bucketListDining) {
      DiningItem diningItem = new DiningItem();
      diningItem.setId(bucketListDining.id());
      diningItem.setUrl(bucketListDining.url());
      diningItem.setAddress(bucketListDining.address());
      diningItem.setCity(bucketListDining.city());
      diningItem.setCountry(bucketListDining.country());
      diningItem.setDescription(bucketListDining.description());
      diningItem.setName(bucketListDining.name());
      diningItem.setPhoneNumber(bucketListDining.phone());
      diningItem.setPriceRange(bucketListDining.priceRange());
      return diningItem;
   }
}

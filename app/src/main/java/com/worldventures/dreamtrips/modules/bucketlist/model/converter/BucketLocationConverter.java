package com.worldventures.dreamtrips.modules.bucketlist.model.converter;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketLocation;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class BucketLocationConverter implements Converter<com.worldventures.dreamtrips.api.bucketlist.model.BucketListLocation, BucketLocation> {

   @Override
   public BucketLocation convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.bucketlist.model.BucketListLocation apiLocation) {
      BucketLocation location = new BucketLocation();
      location.setName(apiLocation.name());
      location.setDescription(apiLocation.description());
      location.setUrl(apiLocation.url());
      return location;
   }

   @Override
   public Class<com.worldventures.dreamtrips.api.bucketlist.model.BucketListLocation> sourceClass() {
      return com.worldventures.dreamtrips.api.bucketlist.model.BucketListLocation.class;
   }

   @Override
   public Class<BucketLocation> targetClass() {
      return BucketLocation.class;
   }
}

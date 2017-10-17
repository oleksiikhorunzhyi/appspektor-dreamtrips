package com.worldventures.dreamtrips.social.ui.bucketlist.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListLocation;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketLocation;

import io.techery.mappery.MapperyContext;

public class BucketLocationConverter implements Converter<BucketListLocation, BucketLocation> {

   @Override
   public BucketLocation convert(MapperyContext mapperyContext, BucketListLocation apiLocation) {
      BucketLocation location = new BucketLocation();
      location.setName(apiLocation.name());
      location.setDescription(apiLocation.description());
      location.setUrl(apiLocation.url());
      return location;
   }

   @Override
   public Class<BucketListLocation> sourceClass() {
      return BucketListLocation.class;
   }

   @Override
   public Class<BucketLocation> targetClass() {
      return BucketLocation.class;
   }
}

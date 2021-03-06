package com.worldventures.dreamtrips.social.ui.bucketlist.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketPhotoBody;
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketPhotoBody;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;

import io.techery.mappery.MapperyContext;

public class BucketPhotoBodyConverter implements Converter<BucketPhoto, BucketPhotoBody> {

   @Override
   public Class<BucketPhoto> sourceClass() {
      return BucketPhoto.class;
   }

   @Override
   public Class<BucketPhotoBody> targetClass() {
      return BucketPhotoBody.class;
   }

   @Override
   public BucketPhotoBody convert(MapperyContext mapperyContext, BucketPhoto bucketPhoto) {
      ImmutableBucketPhotoBody.Builder apiPhoto = ImmutableBucketPhotoBody.builder();
      apiPhoto.originUrl(bucketPhoto.getOriginUrl());
      return apiPhoto.build();
   }
}

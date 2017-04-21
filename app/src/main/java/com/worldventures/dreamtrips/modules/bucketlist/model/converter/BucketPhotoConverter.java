package com.worldventures.dreamtrips.modules.bucketlist.model.converter;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class BucketPhotoConverter implements Converter<com.worldventures.dreamtrips.api.bucketlist.model.BucketPhoto, BucketPhoto> {

   @Override
   public Class<com.worldventures.dreamtrips.api.bucketlist.model.BucketPhoto> sourceClass() {
      return com.worldventures.dreamtrips.api.bucketlist.model.BucketPhoto.class;
   }

   @Override
   public Class<BucketPhoto> targetClass() {
      return BucketPhoto.class;
   }

   @Override
   public BucketPhoto convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.bucketlist.model.BucketPhoto apiPhoto) {
      BucketPhoto photo = new BucketPhoto();
      photo.setUid(apiPhoto.uid());
      photo.setUrl(apiPhoto.url());
      photo.setOriginUrl(apiPhoto.originUrl());
      return photo;
   }
}

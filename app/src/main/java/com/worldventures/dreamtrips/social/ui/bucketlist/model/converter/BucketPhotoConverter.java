package com.worldventures.dreamtrips.social.ui.bucketlist.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;

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

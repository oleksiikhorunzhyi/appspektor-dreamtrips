package com.worldventures.dreamtrips.modules.bucketlist.model.converter;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketCoverPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class BucketCoverPhotoConverter implements Converter<BucketCoverPhoto, BucketPhoto> {

   @Override
   public Class<BucketCoverPhoto> sourceClass() {
      return BucketCoverPhoto.class;
   }

   @Override
   public Class<BucketPhoto> targetClass() {
      return BucketPhoto.class;
   }

   @Override
   public BucketPhoto convert(MapperyContext mapperyContext, BucketCoverPhoto apiPhoto) {
      BucketPhoto photo = new BucketPhoto();
      photo.setUrl(apiPhoto.url());
      photo.setOriginUrl(apiPhoto.originUrl());
      return photo;
   }
}

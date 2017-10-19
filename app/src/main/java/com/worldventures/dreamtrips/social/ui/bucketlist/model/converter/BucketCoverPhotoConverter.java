package com.worldventures.dreamtrips.social.ui.bucketlist.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketCoverPhoto;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;

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
      photo.setUid(apiPhoto.uid());
      photo.setUrl(apiPhoto.url());
      return photo;
   }
}

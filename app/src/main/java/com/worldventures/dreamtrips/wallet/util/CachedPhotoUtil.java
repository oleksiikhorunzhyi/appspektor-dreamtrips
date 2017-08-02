package com.worldventures.dreamtrips.wallet.util;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;

import javax.inject.Inject;

import timber.log.Timber;

public class CachedPhotoUtil {

   @Inject
   CachedPhotoUtil() {}

   public void removeCachedPhoto(String photoUri) {
      try {
         Fresco.getImagePipeline().evictFromCache(Uri.parse(photoUri));
      } catch (Exception e) {
         Timber.e(e, "");
      }
   }
}

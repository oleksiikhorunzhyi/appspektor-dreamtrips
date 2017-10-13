package com.worldventures.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Pair;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.worldventures.core.ui.util.DrawableUtil;

import rx.Observable;
import rx.Subscriber;

public class ImageUtils {

   public static final String MIME_TYPE_GIF = "image/gif";

   private static final String PATTERN = "%s?width=%d&height=%d";

   private static void setDataSubscriber(Context context, Uri uri, int width, int height, BitmapReceiverListener bitmapReciveListener, BitmapErrorReceiverListener errorReceiverListener) {
      DataSubscriber dataSubscriber = new BaseDataSubscriber<CloseableReference<CloseableBitmap>>() {
         @Override
         public void onNewResultImpl(DataSource<CloseableReference<CloseableBitmap>> dataSource) {
            if (!dataSource.isFinished()) {
               return;
            }
            CloseableReference<CloseableBitmap> imageReference = dataSource.getResult();
            if (imageReference != null) {
               final CloseableReference<CloseableBitmap> closeableReference = imageReference.clone();
               try {
                  CloseableBitmap closeableBitmap = closeableReference.get();
                  Bitmap bitmap = closeableBitmap.getUnderlyingBitmap();
                  if (bitmap != null && !bitmap.isRecycled()) {
                     //you can use bitmap here
                     if (bitmapReciveListener != null) {
                        bitmapReciveListener.onBitmapReceived(bitmap.copy(bitmap.getConfig(), true));
                     }
                  }
               } finally {
                  imageReference.close();
                  closeableReference.close();
               }
            }
         }

         @Override
         protected void onFailureImpl(DataSource<CloseableReference<CloseableBitmap>> dataSource) {
            if (errorReceiverListener != null) {
               errorReceiverListener.onError(dataSource.getFailureCause());
            }
         }
      };
      getBitmap(context, uri, width, height, dataSubscriber);
   }


   private static void getBitmap(Context context, Uri uri, int width, int height, DataSubscriber dataSubscriber) {
      ImagePipeline imagePipeline = Fresco.getImagePipeline();
      ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
      if (width > 0 && height > 0) {
         builder.setResizeOptions(new ResizeOptions(width, height));
      }
      ImageRequest request = builder.build();
      DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(request, context);
      dataSource.subscribe(dataSubscriber, AsyncTask.THREAD_POOL_EXECUTOR);
   }

   public static Observable<Bitmap> getBitmap(Context context, Uri uri, int width, int height) {
      return Observable.create(new Observable.OnSubscribe<Bitmap>() {
         @Override
         public void call(Subscriber<? super Bitmap> subscriber) {
            setDataSubscriber(context, uri, width, height, bitmap -> {
               if (!subscriber.isUnsubscribed()) {
                  subscriber.onNext(bitmap);
                  subscriber.onCompleted();
               }
            }, t -> {
               subscriber.onError(t);
               if (!subscriber.isUnsubscribed()) {
                  subscriber.unsubscribe();
               }
            });
         }
      });
   }

   public static Pair<String, Size> generateUri(DrawableUtil drawableUtil, String baseUri) {
      if (ValidationUtils.isUrl(baseUri)) {
         return new Pair<>(baseUri, drawableUtil.getImageSizeFromUrl(baseUri, DrawableUtil.THUMBNAIL_BIG));
      } else {
         return drawableUtil.compressAndRotateImage(baseUri, DrawableUtil.THUMBNAIL_BIG);
      }
   }

   public static String getImageExtensionFromPath(String path) {
      if (path == null) { return ""; }
      //
      int index = path.lastIndexOf(".");
      //
      if (index < 0) { return ""; }
      return path.substring(index);
   }

   private interface BitmapReceiverListener {
      void onBitmapReceived(Bitmap bitmap);
   }

   private interface BitmapErrorReceiverListener {
      void onError(Throwable t);
   }

   public static String getParametrizedUrl(String url, int width, int height) {
      return String.format(PATTERN, url, width, height);
   }

   public static Bitmap getVideoThumbnail(Context context, long videoGalleryId) {
      return MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), videoGalleryId,
            MediaStore.Video.Thumbnails.MICRO_KIND, new BitmapFactory.Options());
   }
}

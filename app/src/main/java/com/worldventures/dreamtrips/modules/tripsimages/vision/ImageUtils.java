package com.worldventures.dreamtrips.modules.tripsimages.vision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.util.SparseArray;

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
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.Position;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.TagPosition;
import com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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

   public static Observable<ArrayList<PhotoTag>> getRecognizedFaces(Context context, Observable<Bitmap> bitmapObservable) {
      return getRecognizedFacesInternal(context, bitmapObservable).onErrorResumeNext(e -> Observable.just(new ArrayList<>()));
   }

   private static Observable<ArrayList<PhotoTag>> getRecognizedFacesInternal(Context context, Observable<Bitmap> bitmapObservable) {
      Detector detector = new FaceDetector.Builder(context).setTrackingEnabled(false)
            .setLandmarkType(FaceDetector.NO_LANDMARKS)
            .setClassificationType(FaceDetector.NO_CLASSIFICATIONS)
            .setMode(FaceDetector.FAST_MODE)
            .build();

      return bitmapObservable.doOnNext(bitmap -> {
         if (!detector.isOperational()) throw new IllegalStateException();
      })
            .map(bitmap -> new Frame.Builder().setBitmap(bitmap).build())
            .map(frame -> new Pair<>(detector.detect(frame), new RectF(0, 0, frame.getMetadata()
                  .getWidth(), frame.getMetadata().getHeight())))
            .map(pair -> {
               SparseArray<Face> faceSparseArray = pair.first;
               ArrayList<PhotoTag> result = new ArrayList<>();
               for (int i = 0; i < faceSparseArray.size(); i++) {
                  Face face = faceSparseArray.get(faceSparseArray.keyAt(i));
                  PointF position = face.getPosition();
                  float absoluteX = Math.max(position.x, 0.0f);
                  float absoluteY = Math.max(position.y, 0.0f);
                  TagPosition absolute = new TagPosition(
                        (int) absoluteX,
                        (int) absoluteY,
                        (int) absoluteX + (int) face.getWidth(),
                        (int) absoluteY + (int) face.getHeight());
                  TagPosition proportional = CoordinatesTransformer.convertToProportional(absolute, pair.second);
                  float bottomXProportional = Math.min(0.95f, proportional.getBottomRight().getX());
                  float bottomYProportional = Math.min(0.95f, proportional.getBottomRight().getY());
                  proportional = new TagPosition(proportional.getTopLeft(), new Position(bottomXProportional, bottomYProportional));
                  result.add(new PhotoTag(proportional, 0));
               }
               return result;
            })
            .doOnUnsubscribe(() -> detector.release())
            .doOnCompleted(() -> detector.release())
            .doOnError(throwable -> detector.release());
   }

   public static Pair<String, Size> generateUri(DrawableUtil drawableUtil, String baseUri) {
      if (ValidationUtils.isUrl(baseUri)) {
         return new Pair<>(baseUri, drawableUtil.getImageSizeFromUrl(baseUri, DrawableUtil.THUMBNAIL_BIG));
      } else {
         return drawableUtil.compressAndRotateImage(baseUri, DrawableUtil.THUMBNAIL_BIG);
      }
   }

   public static String getImageExtensionFromPath(String path) {
      if (path == null) return "";
      //
      int index = path.lastIndexOf(".");
      //
      if (index < 0) return "";
      return path.substring(index);
   }

   public static Bitmap fromFile(String filePath) {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      return BitmapFactory.decodeFile(filePath, options);
   }

   public static File saveToFile(Context context, Bitmap bitmap) throws IOException {
      FileOutputStream out = null;
      File file = createFile(context);
      if (file == null) throw new IOException("Bitmap cannot be saved");
      try {
         out = new FileOutputStream(file);
         bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
         // PNG is a lossless format, the compression factor (100) is ignored
      } finally {
         if (out != null) {
            out.close();
         }
      }
      return file;
   }

   @Nullable
   private static File createFile(Context context) {
      long time = System.currentTimeMillis();
      String filePath = context.getCacheDir()
            .getAbsolutePath() + File.separator + "bitmaps" + File.separator + time + ".png";
      File file = new File(filePath);
      if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
         return file;
      }
      return null;
   }

   public static Bitmap scaleBitmap(Bitmap source, int imageSize) {
      return Bitmap.createScaledBitmap(source, imageSize, imageSize, true);
   }

   public static Bitmap toMonochromeBitmap(Bitmap source) {
      Bitmap bmpMonochrome = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

      Canvas canvas = new Canvas(bmpMonochrome);
      ColorMatrix ma = new ColorMatrix();
      ma.setSaturation(0);
      Paint paint = new Paint();
      paint.setColorFilter(new ColorMatrixColorFilter(ma));
      canvas.drawBitmap(source, 0, 0, paint);

      return bmpMonochrome;
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
}

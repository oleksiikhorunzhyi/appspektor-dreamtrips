package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Pair;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.CoordinatesTransformer;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.Position;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.TagPosition;

import java.util.ArrayList;
import java.util.Collections;

import rx.Observable;

public final class FaceRecognitionUtils {

   private FaceRecognitionUtils() {
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
         if (!detector.isOperational()) {
            throw new IllegalStateException();
         }
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
               Collections.sort(result);
               return result;
            })
            .doOnUnsubscribe(() -> detector.release())
            .doOnCompleted(() -> detector.release())
            .doOnError(throwable -> detector.release());
   }
}

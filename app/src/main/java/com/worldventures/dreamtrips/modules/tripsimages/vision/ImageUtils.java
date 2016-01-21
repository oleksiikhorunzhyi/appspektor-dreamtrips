package com.worldventures.dreamtrips.modules.tripsimages.vision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ImageUtils {

    private static void setDataSubscriber(Context context, Uri uri, int width, int height, BitmapReceiveListener bitmapReciveListener) {
        DataSubscriber dataSubscriber = new BaseDataSubscriber<CloseableReference<CloseableBitmap>>() {
            @Override
            public void onNewResultImpl(
                    DataSource<CloseableReference<CloseableBitmap>> dataSource) {
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
                                bitmapReciveListener.onBitmapReceived(bitmap);
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
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(request, context);
        dataSource.subscribe(dataSubscriber, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static Observable<Bitmap> getBitmap(Context context, Uri uri, int width, int height) {
        return Observable.create(subscriber -> setDataSubscriber(context, uri, width, height, bitmap -> {
            subscriber.onNext(bitmap);
            subscriber.onCompleted();
            subscriber.unsubscribe();
        }));
    }

    public static Observable<ArrayList<PhotoTag>> getRecognizedFaces(Context context, Observable<Bitmap> bitmapObservable) {

        Detector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.NO_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .setClassificationType(FaceDetector.NO_CLASSIFICATIONS)
                .build();

        return bitmapObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(b -> detector.isOperational())
                .map(bitmap -> new Frame.Builder().setBitmap(bitmap).build())
                .map(frame -> new Pair<>(detector.detect(frame), new RectF(0, 0, frame.getMetadata().getWidth(), frame.getMetadata().getHeight())))
                .map(pair -> {
                    SparseArray<Face> faceSparseArray = pair.first;
                    ArrayList<PhotoTag> result = new ArrayList<>();
                    for (int i = 0; i < faceSparseArray.size(); i++) {
                        Face face = faceSparseArray.get(faceSparseArray.keyAt(i));
                        PointF position = face.getPosition();
                        float absoluteX = Math.max(position.x, 0);
                        float absoluteY = Math.max(position.y, 0);
                        PhotoTag.TagPosition absolute = new PhotoTag.TagPosition(
                                (int) absoluteX, (int) absoluteY,
                                (int) absoluteX + (int) face.getWidth(), (int) absoluteY + (int) face.getHeight());
                        PhotoTag.TagPosition proportional = CoordinatesTransformer.convertToProportional(absolute, pair.second);
                        result.add(new PhotoTag(proportional, new User()));
                    }
                    return result;
                })
                .doOnCompleted(detector::release)
                .doOnError(throwable -> Timber.d(throwable, ""));
    }

    private interface BitmapReceiveListener {
        void onBitmapReceived(Bitmap bitmap);
    }
}

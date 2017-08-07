package com.worldventures.dreamtrips.modules.tripsimages.view.custom;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.util.Utils;
import com.worldventures.dreamtrips.modules.tripsimages.view.ImageUtils;

import java.io.IOException;

import rx.functions.Action0;
import rx.functions.Action1;

import static com.worldventures.dreamtrips.core.utils.GraphicUtils.createResizeImageRequest;
import static com.worldventures.dreamtrips.core.utils.GraphicUtils.parseUri;

public class ImageryView extends ScaleImageView {

   private Action0 onFinalImageSetAction;
   private Action1<Throwable> onErrorAction;

   public ImageryView(Context context) {
      super(context);
   }

   public ImageryView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public void loadImage(String imageUrl) {
      if (getMeasuredWidth() > 0) {
         loadImageInternal(imageUrl);
      } else {
         getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
               getViewTreeObserver().removeOnGlobalLayoutListener(this);
               loadImageInternal(imageUrl);
            }
         });
      }
   }

   private void loadImageInternal(String url) {
      int size = Math.max(getMeasuredHeight(), getMeasuredWidth());
      int thumbSize = getResources().getDimensionPixelSize(R.dimen.photo_thumb_size);
      PipelineDraweeControllerBuilder draweeControllerBuilder = Fresco.newDraweeControllerBuilder()
            .setImageRequest(createResizeImageRequest(parseUri(ImageUtils.getParametrizedUrl(url, size, size)), size, size))
            .setControllerListener(new BaseControllerListener<ImageInfo>() {
               @Override
               public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                  super.onFinalImageSet(id, imageInfo, animatable);
                  if (onFinalImageSetAction != null) onFinalImageSetAction.call();
               }

               @Override
               public void onFailure(String id, Throwable throwable) {
                  if (Utils.isConnected(getContext()) && throwable instanceof IOException) {
                     throwable = new Exception("Could not load image");
                  }
                  if (onErrorAction != null) onErrorAction.call(throwable);
               }
            });

      draweeControllerBuilder.setLowResImageRequest(ImageRequest.fromUri(ImageUtils.getParametrizedUrl(url, thumbSize, thumbSize)));
      setController(draweeControllerBuilder.build());
   }

   public void setOnFinalImageSetAction(Action0 onFinalImageSetAction) {
      this.onFinalImageSetAction = onFinalImageSetAction;
   }

   public void setOnErrorAction(Action1<Throwable> onErrorAction) {
      this.onErrorAction = onErrorAction;
   }
}

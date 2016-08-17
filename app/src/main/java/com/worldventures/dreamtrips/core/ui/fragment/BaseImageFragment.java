package com.worldventures.dreamtrips.core.ui.fragment;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_image_details)
public class BaseImageFragment<T extends ImagePathHolder> extends BaseFragmentWithArgs<BaseImagePresenter<T>, ImageBundle<T>> implements BaseImagePresenter.View {

   @InjectView(R.id.imageViewTripImage) protected SimpleDraweeView ivImage;
   @InjectView(R.id.progressBarImage) protected ProgressBar progressBar;

   private ControllerListener controllerListener;

   @Override
   protected BaseImagePresenter<T> createPresenter(Bundle savedInstanceState) {
      return new BaseImagePresenter<>(getArgs());
   }

   @Override
   public void setSize(boolean fullscreen) {
      if (fullscreen) {
         ivImage.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
      } else {
         ivImage.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
      }
      ivImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
         @Override
         public void onGlobalLayout() {
            if (ivImage != null) getPresenter().onImageReady(ivImage.getWidth(), ivImage.getHeight());
            //
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
               ivImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
               ivImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
         }
      });
   }

   @Override
   public void setImage(Uri imageUri) {
      controllerListener = new BaseControllerListener<ImageInfo>() {
         @Override
         public void onSubmit(String id, Object callerContext) {
            progressBar.setVisibility(View.VISIBLE);
         }

         @Override
         public void onFailure(String id, Throwable throwable) {
            if (!isAdded()) return;
            progressBar.setVisibility(View.GONE);
         }

         @Override
         public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            if (!isAdded()) return;
            progressBar.setVisibility(View.GONE);
         }
      };
      DraweeController draweeController = Fresco.newDraweeControllerBuilder()
            .setUri(imageUri)
            .setControllerListener(controllerListener)
            .build();
      ivImage.setController(draweeController);
   }

   @OnClick(R.id.imageViewTripImage)
   void onImageClick() {
      getPresenter().onImageClicked();
   }

   @Override
   public void onDestroyView() {
      if (ivImage != null && ivImage.getController() != null && controllerListener != null) {
         ((AbstractDraweeController) ivImage.getController()).removeControllerListener(controllerListener);
         ivImage.getController().onDetach();
      }
      super.onDestroyView();
   }

}


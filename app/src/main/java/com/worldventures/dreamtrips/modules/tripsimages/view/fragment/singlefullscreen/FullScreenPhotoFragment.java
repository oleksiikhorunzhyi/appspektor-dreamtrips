package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen;

import android.graphics.drawable.Animatable;
import android.os.Build;
import android.view.ViewTreeObserver;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.PhotosShareDialog;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.SocialFullScreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.ScaleImageView;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public abstract class FullScreenPhotoFragment<PRESENTER extends FullScreenPresenter<T, ? extends FullScreenPresenter.View>, T extends IFullScreenObject> extends RxBaseFragmentWithArgs<PRESENTER, FullScreenPhotoBundle> implements SocialFullScreenPresenter.View {

   @InjectView(R.id.iv_image) protected ScaleImageView ivImage;

   @Inject @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator;

   @Override
   public void setContent(IFullScreenObject photo) {
      loadImage(photo.getFSImage());
   }

   private void loadImage(Image image) {
      String lowUrl = image.getThumbUrl(getResources());
      ivImage.requestLayout();
      ViewTreeObserver viewTreeObserver = ivImage.getViewTreeObserver();
      viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
         @Override
         public void onGlobalLayout() {
            if (ivImage != null) {
               int size = Math.max(ivImage.getWidth(), ivImage.getHeight());
               PipelineDraweeControllerBuilder draweeControllerBuilder = Fresco.newDraweeControllerBuilder()
                     .setImageRequest(ImageRequest.fromUri(image.getUrl(size, size)))
                     .setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                           super.onFinalImageSet(id, imageInfo, animatable);
                           onImageGlobalLayout();
                        }

                        @Override
                        public void onFailure(String id, Throwable throwable) {
                           getPresenter().onCouldNotLoadImage(throwable);
                        }
                     });
               if (getPresenter().isConnected())
                  draweeControllerBuilder.setLowResImageRequest(ImageRequest.fromUri(lowUrl));
               DraweeController draweeController = draweeControllerBuilder.build();
               ivImage.setController(draweeController);

               ViewTreeObserver viewTreeObserver = ivImage.getViewTreeObserver();
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                  viewTreeObserver.removeOnGlobalLayoutListener(this);
               } else {
                  viewTreeObserver.removeGlobalOnLayoutListener(this);
               }
            }
         }
      });
   }

   protected void onImageGlobalLayout() {
   }

   @Override
   public void onDestroyView() {
      if (ivImage != null && ivImage.getController() != null) ivImage.getController().onDetach();
      super.onDestroyView();
   }

   @Override
   public void openShare(String imageUrl, String text, @ShareType String type) {
      ShareBundle data = new ShareBundle();
      data.setImageUrl(imageUrl);
      data.setText(text == null ? "" : text);
      data.setShareType(type);
      router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity().data(data).build());
   }

   @Override
   public void openUser(UserBundle bundle) {
      router.moveTo(routeCreator.createRoute(bundle.getUser().getId()), NavigationConfigBuilder.forActivity()
            .data(bundle)
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build());
   }

   @Optional
   @OnClick(R.id.iv_share)
   public void actionShare() {
      getPresenter().onShareAction();
   }

   @Override
   public void onShowShareOptions() {
      new PhotosShareDialog(getActivity(), type -> getPresenter().onShareOptionChosen(type)).show();
   }

   @Override
   public boolean onApiError(ErrorResponse errorResponse) {
      return false;
   }

   @Override
   public void onApiCallFailed() {

   }
}

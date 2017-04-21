package com.worldventures.dreamtrips.core.ui.fragment;

import android.net.Uri;

import com.techery.spares.utils.delegate.ImagePresenterClickEventDelegate;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import javax.inject.Inject;

public class BaseImagePresenter<T extends ImagePathHolder> extends Presenter<BaseImagePresenter.View> {

   protected final T image;
   protected final boolean fullScreen;
   @Inject ImagePresenterClickEventDelegate imagePresenterClickEventDelegate;

   public BaseImagePresenter(ImageBundle<T> bundle) {
      image = bundle.imagePathHolder;
      fullScreen = bundle.fullScreen;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      view.setSize(fullScreen);
   }

   public void onImageReady(int width, int height) {
      view.setImage(createUri(image, width, height));
   }

   protected Uri createUri(T image, int width, int height) {
      return Uri.parse(image.getImagePath());
   }

   public void onImageClicked() {
      imagePresenterClickEventDelegate.post(image);
   }

   public interface View extends Presenter.View {
      void setSize(boolean fullscreen);

      void setImage(Uri imageUri);
   }
}

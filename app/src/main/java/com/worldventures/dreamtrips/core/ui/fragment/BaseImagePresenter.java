package com.worldventures.dreamtrips.core.ui.fragment;

import android.net.Uri;

import com.worldventures.dreamtrips.core.utils.events.ImageClickedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class BaseImagePresenter<T extends ImagePathHolder> extends Presenter<BaseImagePresenter.View> {

   protected final T image;
   protected final boolean fullScreen;

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
      eventBus.post(new ImageClickedEvent(image));
   }

   //

   public interface View extends Presenter.View {
      void setSize(boolean fullscreen);
      void setImage(Uri imageUri);
   }

}

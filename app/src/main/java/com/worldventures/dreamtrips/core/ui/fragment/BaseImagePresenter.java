package com.worldventures.dreamtrips.core.ui.fragment;

import android.net.Uri;

import com.worldventures.core.model.ImagePathHolder;
import com.worldventures.core.utils.ImageUtils;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.util.event_delegate.ImagePresenterClickEventDelegate;

import javax.inject.Inject;

public class BaseImagePresenter extends Presenter<BaseImagePresenter.View> {

   protected final ImagePathHolder image;
   protected final boolean fullScreen;
   @Inject ImagePresenterClickEventDelegate imagePresenterClickEventDelegate;

   public BaseImagePresenter(ImageBundle<ImagePathHolder> bundle) {
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

   protected Uri createUri(ImagePathHolder image, int width, int height) {
      return Uri.parse(ImageUtils.getParametrizedUrl(image.getImagePath(), width, height));
   }

   public void onImageClicked() {
      imagePresenterClickEventDelegate.post(image);
   }

   public interface View extends Presenter.View {
      void setSize(boolean fullscreen);

      void setImage(Uri imageUri);
   }
}

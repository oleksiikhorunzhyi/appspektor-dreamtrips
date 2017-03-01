package com.worldventures.dreamtrips.wallet.util;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class AnimateGIFUtil {

   private AnimateGIFUtil() {}

   public static void setupAnimateGIFbyFresco(SimpleDraweeView draweeView, int imageGifResId) {
      ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithResourceId(imageGifResId);
      DraweeController draweeController = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequestBuilder.build())
            .setAutoPlayAnimations(true)
            .build();
      draweeView.setController(draweeController);
   }
}

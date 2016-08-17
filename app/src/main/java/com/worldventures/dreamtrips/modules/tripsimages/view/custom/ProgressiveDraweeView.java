package com.worldventures.dreamtrips.modules.tripsimages.view.custom;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class ProgressiveDraweeView extends SimpleDraweeView {

   public ProgressiveDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
      super(context, hierarchy);
   }

   public ProgressiveDraweeView(Context context) {
      super(context);
   }

   public ProgressiveDraweeView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }


   @Override
   public void setImageURI(Uri uri) {
      loadProgressiveImage(uri);
   }

   private void loadProgressiveImage(Uri uri) {
      ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setProgressiveRenderingEnabled(true).build();
      DraweeController controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .setOldController(getController())
            .build();
      setController(controller);
   }
}

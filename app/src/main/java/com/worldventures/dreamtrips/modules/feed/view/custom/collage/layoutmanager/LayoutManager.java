package com.worldventures.dreamtrips.modules.feed.view.custom.collage.layoutmanager;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageItem;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageView;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public abstract class LayoutManager {

   protected static final int LANDSCAPE = 0;
   protected static final int PORTRAIT = 1;
   protected static final int SQUARE = 2;

   @Retention(RetentionPolicy.SOURCE)
   @IntDef({LANDSCAPE, PORTRAIT, SQUARE})
   private @interface PhotoType {}

   protected Context context;
   protected List<CollageItem> items;

   protected int halfPadding;
   protected float textSize;
   protected int iconResId;

   public void initialize(Context context, List<CollageItem> items) {
      this.context = context;
      this.items = items;
   }

   public void setAttributes(int padding, float textSize, @DrawableRes int iconResId) {
      halfPadding = padding / 2;
      this.textSize = textSize;
      this.iconResId = iconResId;
   }

   public abstract List<View> getLocatedViews(int holderSide, CollageView.ItemClickListener itemClickListener);

   public abstract Size getHolderSize();

   @PhotoType
   protected int getType(CollageItem item) {
      if (item.width > item.height) {
         return LANDSCAPE;
      } else if (item.width < item.height) {
         return PORTRAIT;
      } else {
         return SQUARE;
      }
   }

   protected FrameLayout.LayoutParams getLayoutParams(int width, int height) {
      return getLayoutParams(width, height, Gravity.NO_GRAVITY);
   }

   protected FrameLayout.LayoutParams getLayoutParams(int width, int height, int gravity) {
      return new FrameLayout.LayoutParams(width, height, gravity);
   }

   protected Rect getPaddings(int left, int top, int right, int bottom) {
      return new Rect(left, top, right, bottom);
   }

   protected View getImageView(final int position, FrameLayout.LayoutParams params) {
      return getImageView(position, params, new Rect(), null);
   }

   protected View getImageView(final int position, FrameLayout.LayoutParams params, CollageView.ItemClickListener itemClickListener) {
      return getImageView(position, params, new Rect(), itemClickListener);
   }

   protected View getImageView(final int position, FrameLayout.LayoutParams params, Rect paddings, CollageView.ItemClickListener itemClickListener) {
      String url = ImageUtils.getParametrizedUrl(items.get(position).url, params.width, params.height);
      ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).build();
      PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .build();
      SimpleDraweeView view = new SimpleDraweeView(context);
      view.setController(controller);
      view.setPadding(paddings.left, paddings.top, paddings.right, paddings.bottom);
      view.setLayoutParams(params);
      view.setOnClickListener(v -> {
         if (itemClickListener != null) itemClickListener.itemClicked(position);
      });
      return view;
   }
}

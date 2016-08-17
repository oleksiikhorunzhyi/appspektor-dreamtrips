package com.worldventures.dreamtrips.modules.feed.view.custom.collage.layoutmanager;

import android.graphics.Rect;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageView;
import com.worldventures.dreamtrips.modules.feed.view.util.blur.BlurPostprocessor;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;
import java.util.List;

class LayoutManagerMany extends LayoutManager {

   private static final int BLUR_RADIUS = 30;
   private static final int BLUR_SAMPLING = 1; //scale canvas before blur

   private Size holderSize;

   @Override
   public List<View> getLocatedViews(int holderSide, CollageView.ItemClickListener itemClickListener) {
      List<View> views = new ArrayList<>(items.size());

      int firstType = getType(items.get(0));
      if (firstType == LANDSCAPE) {
         views.add(getImageView(0, getLayoutParams(holderSide, holderSide * 2 / 3), getPaddings(0, 0, 0, halfPadding), itemClickListener));
         views.add(getImageView(1, getLayoutParams(holderSide / 3, holderSide / 3, Gravity.BOTTOM), getPaddings(0, halfPadding, halfPadding, 0), itemClickListener));
         views.add(getImageView(2, getLayoutParams(holderSide / 3, holderSide / 3, Gravity.BOTTOM | Gravity.CENTER), getPaddings(halfPadding, halfPadding, halfPadding, 0), itemClickListener));
         views.add(getMoreButton(3, getLayoutParams(holderSide / 3, holderSide / 3, Gravity.BOTTOM | Gravity.RIGHT), getPaddings(halfPadding, halfPadding, 0, 0), itemClickListener));
      } else if (firstType == PORTRAIT) {
         views.add(getImageView(0, getLayoutParams(holderSide * 2 / 3, holderSide), getPaddings(0, 0, halfPadding, 0), itemClickListener));
         views.add(getImageView(1, getLayoutParams(holderSide / 3, holderSide / 3, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, halfPadding), itemClickListener));
         views.add(getImageView(2, getLayoutParams(holderSide / 3, holderSide / 3, Gravity.RIGHT | Gravity.CENTER), getPaddings(halfPadding, halfPadding, 0, halfPadding), itemClickListener));
         views.add(getMoreButton(3, getLayoutParams(holderSide / 3, holderSide / 3, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0), itemClickListener));
      } else {
         views.add(getImageView(0, getLayoutParams(holderSide * 2 / 3, holderSide / 2), getPaddings(0, 0, halfPadding, halfPadding), itemClickListener));
         views.add(getImageView(1, getLayoutParams(holderSide * 2 / 3, holderSide / 2, Gravity.BOTTOM), getPaddings(0, halfPadding, halfPadding, 0), itemClickListener));
         views.add(getImageView(2, getLayoutParams(holderSide / 3, holderSide / 3, Gravity.RIGHT), getPaddings(halfPadding, 0, 0, halfPadding), itemClickListener));
         views.add(getImageView(3, getLayoutParams(holderSide / 3, holderSide / 3, Gravity.RIGHT | Gravity.CENTER), getPaddings(halfPadding, halfPadding, 0, halfPadding), itemClickListener));
         if (items.size() <= 5) {
            views.add(getImageView(4, getLayoutParams(holderSide / 3, holderSide / 3, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0), itemClickListener));
         } else {
            views.add(getMoreButton(3, getLayoutParams(holderSide / 3, holderSide / 3, Gravity.RIGHT | Gravity.BOTTOM), getPaddings(halfPadding, halfPadding, 0, 0), itemClickListener));
         }
      }

      holderSize = new Size(holderSide, holderSide);

      return views;
   }

   @Override
   public Size getHolderSize() {
      return holderSize;
   }

   private View getMoreButton(int position, FrameLayout.LayoutParams params, Rect paddings, CollageView.ItemClickListener itemClickListener) {
      //more image button root
      FrameLayout moreViewRoot = new FrameLayout(context);
      FrameLayout.LayoutParams moreViewRootParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      moreViewRootParams.gravity = params.gravity;
      moreViewRoot.setLayoutParams(moreViewRootParams);
      moreViewRoot.setOnClickListener(v -> {
         if (itemClickListener != null) itemClickListener.moreClicked();
      });

      //blur view
      String url = ImageUtils.getParametrizedUrl(items.get(position).url, params.width, params.height);
      ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
            .setPostprocessor(new BlurPostprocessor(context, BLUR_RADIUS, BLUR_SAMPLING))
            .build();
      PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .build();
      SimpleDraweeView view = new SimpleDraweeView(context);
      view.setController(controller);
      view.setPadding(paddings.left, paddings.top, paddings.right, paddings.bottom);
      params.gravity = Gravity.NO_GRAVITY;
      moreViewRoot.addView(view, params);

      //text
      TextView textView = new TextView(context);
      textView.setTextColor(context.getResources().getColor(android.R.color.white));
      textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
      textView.setText(String.format("+%d", items.size() - position));
      textView.setGravity(Gravity.CENTER);
      textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResId, 0);
      FrameLayout.LayoutParams textViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      textViewParams.gravity = Gravity.CENTER;
      moreViewRoot.addView(textView, textViewParams);

      return moreViewRoot;
   }
}

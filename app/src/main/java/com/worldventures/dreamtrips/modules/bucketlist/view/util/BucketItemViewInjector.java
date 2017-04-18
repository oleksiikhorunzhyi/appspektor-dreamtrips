package com.worldventures.dreamtrips.modules.bucketlist.view.util;

import android.content.Context;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BucketItemViewInjector {

   @InjectView(R.id.imageViewCover) SimpleDraweeView imageViewCover;
   @InjectView(R.id.textViewCategory) TextView textViewCategory;
   @InjectView(R.id.textViewDate) TextView textViewDate;
   @InjectView(R.id.textViewPlace) TextView textViewPlace;

   private Context context;
   private TranslateBucketItemViewInjector translateBucketItemViewInjector;

   public BucketItemViewInjector(View rootView, Context context, SessionHolder<UserSession> appSessionHolder) {
      this.context = context;
      translateBucketItemViewInjector = new TranslateBucketItemViewInjector(rootView, context, appSessionHolder);
      ButterKnife.inject(this, rootView);
   }

   public void processBucketItem(BucketItem bucketItem) {
      String mediumResUrl = BucketItemInfoUtil.getMediumResUrl(context, bucketItem);
      String highResUrl = BucketItemInfoUtil.getHighResUrl(context, bucketItem);
      loadImage(mediumResUrl, highResUrl);
      if (TextUtils.isEmpty(bucketItem.getCategoryName())) {
         textViewCategory.setVisibility(View.GONE);
      } else {
         textViewCategory.setVisibility(View.VISIBLE);
         textViewCategory.setText(bucketItem.getCategoryName());
      }
      if (TextUtils.isEmpty(BucketItemInfoUtil.getPlace(bucketItem))) {
         textViewPlace.setVisibility(View.GONE);
      } else {
         textViewPlace.setVisibility(View.VISIBLE);
         textViewPlace.setText(BucketItemInfoUtil.getPlace(bucketItem));
      }
      textViewDate.setText(BucketItemInfoUtil.getTime(context, bucketItem));
      translateBucketItemViewInjector.processTranslation(bucketItem);
   }

   public void translatePressed() {
      translateBucketItemViewInjector.translatePressed();
   }

   private void loadImage(String lowResUrl, String url) {
      DraweeController draweeController = Fresco.newDraweeControllerBuilder()
            .setLowResImageRequest(ImageRequest.fromUri(lowResUrl))
            .setImageRequest(ImageRequest.fromUri(url))
            .build();
      imageViewCover.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.0f));
      imageViewCover.setController(draweeController);
   }
}

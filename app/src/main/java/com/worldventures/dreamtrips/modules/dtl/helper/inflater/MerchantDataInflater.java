package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.view.View;
import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.internal.Preconditions;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;

public abstract class MerchantDataInflater implements MerchantInflater {

   protected View rootView;
   protected MerchantAttributes merchantAttributes;
   protected List<RatingsReviewClickListener> ratingsReviewClickListener = new ArrayList<>();

   @Override
   public void setView(View rootView) {
      Preconditions.checkNotNull(rootView, "View is null");
      this.rootView = rootView;
      ButterKnife.inject(this, rootView);
   }

   @Override
   public void applyMerchantAttributes(MerchantAttributes merchantAttributes) {
      Preconditions.checkNotNull(rootView, "Root view is not set, call setView() method first");
      this.merchantAttributes = merchantAttributes;
      onMerchantAttributesApply();
   }

   @Override
   public void release() {
      ratingsReviewClickListener.clear();
      ButterKnife.reset(this);
      this.rootView = null;
   }

   @Override
   public void registerRatingsClickListener(RatingsReviewClickListener listener) {
      ratingsReviewClickListener.add(listener);
   }

   protected abstract void onMerchantAttributesApply();

   public void notifyRatingsClickListeners() {
      Queryable.from(ratingsReviewClickListener)
              .filter(listener -> listener != null)
              .forEachR(listener -> listener.onRatingsReviewClick());
   }
}

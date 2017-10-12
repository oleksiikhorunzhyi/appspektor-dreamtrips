package com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.ForeignBucketItemDetailsPresenter;

@Layout(R.layout.layout_bucket_item_details)
public class ForeignBucketDetailsFragment extends BucketDetailsFragment<ForeignBucketItemDetailsPresenter> {

   @Override
   protected ForeignBucketItemDetailsPresenter createPresenter(Bundle savedInstanceState) {
      return new ForeignBucketItemDetailsPresenter(getArgs());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      markAsDone.setVisibility(View.GONE);
   }
}

package com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment;

import android.os.Bundle;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.ForeignBucketListPresenter;

@Layout(R.layout.fragment_foreign_bucket_list)
public class ForeignBucketListFragment extends BucketListFragment<ForeignBucketListPresenter> {

   @Override
   protected boolean isDragEnabled() {
      return false;
   }

   @Override
   protected boolean isSwipeEnabled() {
      return false;
   }

   @Override
   protected ForeignBucketListPresenter createPresenter(Bundle savedInstanceState) {
      BucketItem.BucketType type = (BucketItem.BucketType) getArguments().getSerializable(BUNDLE_TYPE);
      return new ForeignBucketListPresenter(type);
   }
}
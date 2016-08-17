package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class BucketPopularTabsPresenter extends Presenter<Presenter.View> {

   public static final String EXTRA_TYPE = "EXTRA_TYPE";

   public Bundle getBundleForPosition(int position) {
      Bundle args = new Bundle();
      BucketItem.BucketType type = BucketItem.BucketType.values()[position];
      args.putSerializable(BucketPopularTabsPresenter.EXTRA_TYPE, type);
      return args;
   }
}

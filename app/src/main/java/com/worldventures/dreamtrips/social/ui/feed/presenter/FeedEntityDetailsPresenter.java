package com.worldventures.dreamtrips.social.ui.feed.presenter;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Pair;

import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.view.util.FeedEntityContentFragmentFactory;

import javax.inject.Inject;

public class FeedEntityDetailsPresenter extends FeedDetailsPresenter<FeedEntityDetailsPresenter.View> {

   @Inject FeedEntityContentFragmentFactory fragmentFactory;

   private boolean isSlave;

   public FeedEntityDetailsPresenter(FeedItem feedItem, boolean isSlave) {
      super(feedItem);
      this.isSlave = isSlave;
   }

   public void onEntityShownInCell(FeedItem feedItem) {
      // for bucket list tablet landscape orientation (slave mode)
      Pair<Class<? extends Fragment>, Parcelable> entityData = fragmentFactory.create(feedItem);
      if (feedItem.getType() == FeedEntityHolder.Type.BUCKET_LIST_ITEM) {
         ((BucketBundle) entityData.second).setSlave(isSlave);
      }
      view.showDetails(entityData.first, entityData.second);
   }

   @Override
   protected void back() {
      if (!isSlave) {
         view.back();
      }
   }

   public interface View extends FeedDetailsPresenter.View {

      void showDetails(Class<? extends Fragment> fragmentClass, Parcelable extra);
   }
}

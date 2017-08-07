package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.BucketViewPagerBundle;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.BaseImageViewPagerPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.BaseImageViewPagerFragment;

import java.util.List;

@Layout(R.layout.fragment_full_screen_photo_wrapper)
public class BucketPhotoViewPagerFragment extends BaseImageViewPagerFragment<BucketPhotoViewPagerFragment.Presenter, BucketViewPagerBundle> {

   public static class Presenter extends BaseImageViewPagerPresenter<BaseImageViewPagerPresenter.View> {
      private BucketItem bucketItem;

      public Presenter(boolean lastPageReached, int selectedPosition, BucketItem bucketItem) {
         super(lastPageReached, selectedPosition);
         this.bucketItem = bucketItem;
      }

      @Override
      protected List<FragmentItem> getItems() {
         return Queryable.from(bucketItem.getPhotos())
               .map(photo -> new FragmentItem(Route.BUCKET_PHOTO_FULLSCREEN, "", photo))
               .toList();
      }
   }

   @Override
   protected Presenter createPresenter(Bundle savedInstanceState) {
      return new Presenter(true, getArgs().getSelection(), getArgs().getBucketItem());
   }
}

package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.BucketFullscreenBundle;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.BucketViewPagerBundle;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.BaseImageViewPagerPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.BaseImageViewPagerFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

@Layout(R.layout.fragment_full_screen_photo_wrapper)
public class BucketPhotoViewPagerFragment extends BaseImageViewPagerFragment<BucketPhotoViewPagerFragment.Presenter, BucketViewPagerBundle> {

   public static class Presenter extends BaseImageViewPagerPresenter<BaseImageViewPagerPresenter.View> {
      @Inject BucketInteractor bucketInteractor;

      private BucketItem bucketItem;
      private List<BucketPhoto> bucketPhotos;

      public Presenter(boolean lastPageReached, int selectedPosition, BucketItem bucketItem) {
         super(lastPageReached, selectedPosition);
         this.bucketItem = bucketItem;
         this.bucketPhotos = new ArrayList<>(bucketItem.getPhotos());
      }

      @Override
      public void onViewTaken() {
         super.onViewTaken();
         bucketInteractor.deleteItemPhotoPipe()
               .observeSuccess()
               .compose(bindViewToMainComposer())
               .subscribe(this::photoDeleted);
      }

      private void photoDeleted(DeleteItemPhotoCommand deleteItemPhotoCommand) {
         bucketItem = deleteItemPhotoCommand.getResult();
         int index = bucketPhotos.indexOf(deleteItemPhotoCommand.getPhoto());
         bucketPhotos.remove(index);
         view.remove(index);
      }

      @Override
      protected List<FragmentItem> getItems() {
         return Queryable.from(bucketPhotos)
               .map(photo -> new FragmentItem(Route.BUCKET_PHOTO_FULLSCREEN, "", new BucketFullscreenBundle(bucketItem, photo)))
               .toList();
      }
   }

   @Override
   protected Presenter createPresenter(Bundle savedInstanceState) {
      return new Presenter(true, getArgs().getSelection(), getArgs().getBucketItem());
   }
}

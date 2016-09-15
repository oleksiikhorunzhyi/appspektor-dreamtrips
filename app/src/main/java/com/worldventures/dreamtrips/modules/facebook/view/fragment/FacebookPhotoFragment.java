package com.worldventures.dreamtrips.modules.facebook.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldventures.dreamtrips.modules.common.view.fragment.BasePickerFragment;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhoto;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPhotoPresenter;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookPhotoCell;
import com.worldventures.dreamtrips.modules.feed.view.util.GridAutofitLayoutManager;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

public class FacebookPhotoFragment extends BasePickerFragment<FacebookPhotoPresenter> implements FacebookPhotoPresenter.View {

   public static final String BUNDLE_ALBUM_ID = "BUNDLE_ALBUM_ID";

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      //
      picker.addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int itemCount = recyclerView.getLayoutManager().getItemCount();
            int lastVisibleItemPosition = ((GridAutofitLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            getPresenter().scrolled(itemCount, lastVisibleItemPosition);
         }
      });
   }

   @Override
   protected void registerCells() {
      adapter.registerCell(FacebookPhoto.class, FacebookPhotoCell.class);
   }

   @Override
   protected int getPhotosType() {
      return PickImageDelegate.FACEBOOK;
   }

   @Override
   public void onResume() {
      super.onResume();
      if (adapter.getItemCount() == 0) {
         getPresenter().requestPhotos(false);
      }
   }

   @Override
   public void onDestroyView() {
      this.picker.setAdapter(null);
      super.onDestroyView();
   }

   @Override
   protected FacebookPhotoPresenter createPresenter(Bundle savedInstanceState) {
      String albumId = getArgs().getString(BUNDLE_ALBUM_ID);
      return new FacebookPhotoPresenter(albumId);
   }
}
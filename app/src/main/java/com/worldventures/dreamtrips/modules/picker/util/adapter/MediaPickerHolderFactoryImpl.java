package com.worldventures.dreamtrips.modules.picker.util.adapter;


import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.PickerAdapterItemAlbumFacebookBinding;
import com.worldventures.dreamtrips.databinding.PickerAdapterItemPhotoFacebookBinding;
import com.worldventures.dreamtrips.databinding.PickerAdapterItemPhotoGalleryBinding;
import com.worldventures.dreamtrips.databinding.PickerAdapterItemStaticBinding;
import com.worldventures.dreamtrips.databinding.PickerAdapterItemVideoGalleryBinding;
import com.worldventures.dreamtrips.modules.picker.model.FacebookAlbumPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.util.adapter.holder.BaseMediaPickerHolder;
import com.worldventures.dreamtrips.modules.picker.util.adapter.holder.FacebookAlbumPickerHolder;
import com.worldventures.dreamtrips.modules.picker.model.FacebookPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.util.adapter.holder.FacebookPhotoPickerHolder;
import com.worldventures.dreamtrips.modules.picker.model.GalleryPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.GalleryVideoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.IrregularPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.util.adapter.holder.GalleryPhotoHolder;
import com.worldventures.dreamtrips.modules.picker.util.adapter.holder.GalleryVideoHolder;
import com.worldventures.dreamtrips.modules.picker.util.adapter.holder.IrregularPhotoHolder;

public class MediaPickerHolderFactoryImpl implements MediaPickerHolderFactory {
   @Override
   public int type(GalleryPhotoPickerViewModel model) {
      return R.layout.picker_adapter_item_photo_gallery;
   }

   @Override
   public int type(GalleryVideoPickerViewModel model) {
      return R.layout.picker_adapter_item_video_gallery;
   }

   @Override
   public int type(IrregularPhotoPickerViewModel model) {
      return R.layout.picker_adapter_item_static;
   }

   @Override
   public int type(FacebookAlbumPickerViewModel model) {
      return R.layout.picker_adapter_item_album_facebook;
   }

   @Override
   public int type(FacebookPhotoPickerViewModel model) {
      return R.layout.picker_adapter_item_photo_facebook;
   }

   @Override
   public BaseMediaPickerHolder holder(ViewGroup parent, int viewType) {
      final View itemView = LayoutInflater
            .from(parent.getContext()).inflate(viewType, parent, false);
      switch (viewType) {
         case R.layout.picker_adapter_item_photo_gallery:
            final PickerAdapterItemPhotoGalleryBinding photoGalleryBinding = DataBindingUtil.bind(itemView);
            return new GalleryPhotoHolder(photoGalleryBinding);
         case R.layout.picker_adapter_item_video_gallery:
            final PickerAdapterItemVideoGalleryBinding videoGalleryBinding = DataBindingUtil.bind(itemView);
            return new GalleryVideoHolder(videoGalleryBinding);
         case R.layout.picker_adapter_item_static:
            final PickerAdapterItemStaticBinding itemStaticBinding = DataBindingUtil.bind(itemView);
            return new IrregularPhotoHolder(itemStaticBinding);
         case R.layout.picker_adapter_item_album_facebook:
            final PickerAdapterItemAlbumFacebookBinding albumFacebookBinding = DataBindingUtil.bind(itemView);
            return new FacebookAlbumPickerHolder(albumFacebookBinding);
         case R.layout.picker_adapter_item_photo_facebook:
            final PickerAdapterItemPhotoFacebookBinding photoFacebookBinding = DataBindingUtil.bind(itemView);
            return new FacebookPhotoPickerHolder(photoFacebookBinding);
         default:
            throw new IllegalArgumentException();
      }
   }
}

package com.worldventures.core.modules.picker.util.adapter;


import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.core.R;
import com.worldventures.core.databinding.PickerAdapterItemAlbumFacebookBinding;
import com.worldventures.core.databinding.PickerAdapterItemPhotoFacebookBinding;
import com.worldventures.core.databinding.PickerAdapterItemPhotoGalleryBinding;
import com.worldventures.core.databinding.PickerAdapterItemStaticBinding;
import com.worldventures.core.databinding.PickerAdapterItemVideoGalleryBinding;
import com.worldventures.core.modules.picker.util.adapter.holder.BaseMediaPickerHolder;
import com.worldventures.core.modules.picker.util.adapter.holder.FacebookAlbumPickerHolder;
import com.worldventures.core.modules.picker.util.adapter.holder.FacebookPhotoPickerHolder;
import com.worldventures.core.modules.picker.util.adapter.holder.GalleryPhotoHolder;
import com.worldventures.core.modules.picker.util.adapter.holder.GalleryVideoHolder;
import com.worldventures.core.modules.picker.util.adapter.holder.IrregularPhotoHolder;
import com.worldventures.core.modules.picker.viewmodel.FacebookAlbumPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.FacebookPhotoPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.GalleryPhotoPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.GalleryVideoPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.IrregularPhotoPickerViewModel;

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
      if (viewType == R.layout.picker_adapter_item_photo_gallery) {
         final PickerAdapterItemPhotoGalleryBinding photoGalleryBinding = DataBindingUtil.bind(itemView);
         return new GalleryPhotoHolder(photoGalleryBinding);
      } else if (viewType == R.layout.picker_adapter_item_video_gallery) {
         final PickerAdapterItemVideoGalleryBinding videoGalleryBinding = DataBindingUtil.bind(itemView);
         return new GalleryVideoHolder(videoGalleryBinding);
      } else if (viewType == R.layout.picker_adapter_item_static) {
         final PickerAdapterItemStaticBinding itemStaticBinding = DataBindingUtil.bind(itemView);
         return new IrregularPhotoHolder(itemStaticBinding);
      } else if (viewType == R.layout.picker_adapter_item_album_facebook) {
         final PickerAdapterItemAlbumFacebookBinding albumFacebookBinding = DataBindingUtil.bind(itemView);
         return new FacebookAlbumPickerHolder(albumFacebookBinding);
      } else if (viewType == R.layout.picker_adapter_item_photo_facebook) {
         final PickerAdapterItemPhotoFacebookBinding photoFacebookBinding = DataBindingUtil.bind(itemView);
         return new FacebookPhotoPickerHolder(photoFacebookBinding);
      } else {
         throw new IllegalArgumentException();
      }
   }
}

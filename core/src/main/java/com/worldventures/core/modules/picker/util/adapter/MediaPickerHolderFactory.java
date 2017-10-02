package com.worldventures.core.modules.picker.util.adapter;

import android.view.ViewGroup;

import com.worldventures.core.modules.picker.viewmodel.FacebookAlbumPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.FacebookPhotoPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.GalleryPhotoPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.GalleryVideoPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.IrregularPhotoPickerViewModel;
import com.worldventures.core.modules.picker.util.adapter.holder.BaseMediaPickerHolder;


public interface MediaPickerHolderFactory {

   BaseMediaPickerHolder holder(ViewGroup parent, int viewType);

   int type(GalleryPhotoPickerViewModel model);

   int type(GalleryVideoPickerViewModel model);

   int type(IrregularPhotoPickerViewModel model);

   int type(FacebookAlbumPickerViewModel model);

   int type(FacebookPhotoPickerViewModel model);
}

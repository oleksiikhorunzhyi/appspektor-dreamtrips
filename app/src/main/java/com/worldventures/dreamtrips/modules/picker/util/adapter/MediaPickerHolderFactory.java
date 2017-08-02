package com.worldventures.dreamtrips.modules.picker.util.adapter;

import android.view.ViewGroup;

import com.worldventures.dreamtrips.modules.picker.model.FacebookAlbumPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.FacebookPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.GalleryPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.GalleryVideoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.IrregularPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.util.adapter.holder.BaseMediaPickerHolder;


public interface MediaPickerHolderFactory {

   BaseMediaPickerHolder holder(ViewGroup parent, int viewType);

   int type(GalleryPhotoPickerViewModel model);

   int type(GalleryVideoPickerViewModel model);

   int type(IrregularPhotoPickerViewModel model);

   int type(FacebookAlbumPickerViewModel model);

   int type(FacebookPhotoPickerViewModel model);
}

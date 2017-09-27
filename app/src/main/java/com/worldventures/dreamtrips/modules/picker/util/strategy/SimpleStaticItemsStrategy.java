package com.worldventures.dreamtrips.modules.picker.util.strategy;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.picker.model.GalleryPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.GalleryMediaPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.IrregularPhotoPickerViewModel;

import java.util.ArrayList;
import java.util.List;

public class SimpleStaticItemsStrategy implements MediaPickerStaticItemsStrategy {

   @Override
   public List<GalleryMediaPickerViewModel> provideStaticItems() {
      List<GalleryMediaPickerViewModel> staticItems = new ArrayList<>();
      staticItems.add(new IrregularPhotoPickerViewModel(IrregularPhotoPickerViewModel.CAMERA, R.drawable.ic_picker_camera, R.string.camera, R.color.share_camera_color));
      staticItems.add(new IrregularPhotoPickerViewModel(IrregularPhotoPickerViewModel.FACEBOOK, R.drawable.fb_logo, R.string.add_from_facebook, R.color.facebook_color));
      return staticItems;
   }

   @Override
   public boolean isExtraItemAvailable() {
      return false;
   }

   @Override
   public GalleryPhotoPickerViewModel provideExtraItem() {
      throw new UnsupportedOperationException("Simple strategy doesn't support extra item");
   }
}

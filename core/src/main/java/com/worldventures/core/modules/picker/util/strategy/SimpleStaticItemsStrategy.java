package com.worldventures.core.modules.picker.util.strategy;


import com.worldventures.core.R;
import com.worldventures.core.modules.picker.viewmodel.GalleryMediaPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.GalleryPhotoPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.IrregularPhotoPickerViewModel;

import java.util.ArrayList;
import java.util.List;

public class SimpleStaticItemsStrategy implements MediaPickerStaticItemsStrategy {

   @Override
   public List<GalleryMediaPickerViewModel> provideStaticItems() {
      List<GalleryMediaPickerViewModel> staticItems = new ArrayList<>();
      staticItems.add(new IrregularPhotoPickerViewModel(IrregularPhotoPickerViewModel.CAMERA, R.drawable.ic_picker_item_static_camera,
            R.string.picker_static_take_from_camera, R.color.picker_static_camera));
      staticItems.add(new IrregularPhotoPickerViewModel(IrregularPhotoPickerViewModel.FACEBOOK,
            R.drawable.ic_picker_item_static_facebook, R.string.picker_static_add_from_fb, R.color.picker_static_facebook));
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

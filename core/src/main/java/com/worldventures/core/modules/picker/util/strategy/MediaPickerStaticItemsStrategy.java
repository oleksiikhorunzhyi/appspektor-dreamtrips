package com.worldventures.core.modules.picker.util.strategy;


import com.worldventures.core.modules.picker.viewmodel.GalleryMediaPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.GalleryPhotoPickerViewModel;

import java.util.List;

public interface MediaPickerStaticItemsStrategy {
   List<GalleryMediaPickerViewModel> provideStaticItems();

   boolean isExtraItemAvailable();

   GalleryPhotoPickerViewModel provideExtraItem();
}

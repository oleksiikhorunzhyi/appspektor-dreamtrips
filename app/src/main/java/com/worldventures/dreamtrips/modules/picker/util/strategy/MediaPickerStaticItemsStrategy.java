package com.worldventures.dreamtrips.modules.picker.util.strategy;


import com.worldventures.dreamtrips.modules.picker.model.GalleryPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.GalleryMediaPickerViewModel;

import java.util.List;

public interface MediaPickerStaticItemsStrategy {
   List<GalleryMediaPickerViewModel> provideStaticItems();

   boolean isExtraItemAvailable();

   GalleryPhotoPickerViewModel provideExtraItem();
}

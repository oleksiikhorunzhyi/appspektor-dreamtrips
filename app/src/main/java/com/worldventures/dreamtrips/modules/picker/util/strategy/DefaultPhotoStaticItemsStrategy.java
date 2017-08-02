package com.worldventures.dreamtrips.modules.picker.util.strategy;


import com.worldventures.dreamtrips.modules.picker.model.GalleryPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.GalleryMediaPickerViewModel;

import java.util.ArrayList;
import java.util.List;

public class DefaultPhotoStaticItemsStrategy extends SimpleStaticItemsStrategy {
   private final String defaultPhotoUrl;

   public DefaultPhotoStaticItemsStrategy(String defaultPhotoUrl) {
      this.defaultPhotoUrl = defaultPhotoUrl;
   }

   @Override
   public List<GalleryMediaPickerViewModel> provideStaticItems() {
      final List<GalleryMediaPickerViewModel> appendedStaticList = new ArrayList<>();
      appendedStaticList.addAll(super.provideStaticItems());
      appendedStaticList.add(provideExtraItem());
      return appendedStaticList;
   }

   @Override
   public boolean isExtraItemAvailable() {
      return true;
   }

   @Override
   public GalleryPhotoPickerViewModel provideExtraItem() {
      return new GalleryPhotoPickerViewModel(defaultPhotoUrl);
   }
}

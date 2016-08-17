package com.worldventures.dreamtrips.modules.common.event;

import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;

public class PhotoPickedEvent {

   public final BasePhotoPickerModel model;

   public PhotoPickedEvent(BasePhotoPickerModel model) {
      this.model = model;
   }
}

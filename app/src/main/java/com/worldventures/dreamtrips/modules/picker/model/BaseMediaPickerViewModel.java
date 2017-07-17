package com.worldventures.dreamtrips.modules.picker.model;

import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.picker.util.adapter.MediaPickerHolderFactory;


public abstract class BaseMediaPickerViewModel implements MediaPickerModel {
   private MediaAttachment.Source source;

   public  abstract int type(MediaPickerHolderFactory typeFactory);

   public abstract void setPickedTime(long pickedTime);

   @Override
   public MediaAttachment.Source getSource() {
      return source;
   }

   public void setSource(MediaAttachment.Source source) {
      this.source = source;
   }
}

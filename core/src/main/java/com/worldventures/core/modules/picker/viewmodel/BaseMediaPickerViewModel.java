package com.worldventures.core.modules.picker.viewmodel;

import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.MediaPickerModel;
import com.worldventures.core.modules.picker.util.adapter.MediaPickerHolderFactory;


public abstract class BaseMediaPickerViewModel implements MediaPickerModel {
   private MediaPickerAttachment.Source source;

   public abstract int type(MediaPickerHolderFactory typeFactory);

   public abstract void setPickedTime(long pickedTime);

   @Override
   public MediaPickerAttachment.Source getSource() {
      return source;
   }

   public void setSource(MediaPickerAttachment.Source source) {
      this.source = source;
   }
}

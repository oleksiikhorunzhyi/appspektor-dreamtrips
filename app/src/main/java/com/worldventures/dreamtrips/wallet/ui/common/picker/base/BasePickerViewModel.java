package com.worldventures.dreamtrips.wallet.ui.common.picker.base;

import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;


public abstract class BasePickerViewModel implements MediaPickerModel {
   private MediaAttachment.Source source;

   public  abstract int type(WalletPickerHolderFactory typeFactory);

   public abstract void setPickedTime(long pickedTime);

   @Override
   public MediaAttachment.Source getSource() {
      return source;
   }

   public void setSource(MediaAttachment.Source source) {
      this.source = source;
   }
}

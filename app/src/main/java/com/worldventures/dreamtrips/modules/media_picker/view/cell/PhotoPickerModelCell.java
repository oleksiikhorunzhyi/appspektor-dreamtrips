package com.worldventures.dreamtrips.modules.media_picker.view.cell;

import android.view.View;

import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.adapter_item_photo_pick)
public class PhotoPickerModelCell extends MediaPickerModelCell<PhotoPickerModel> {

   public PhotoPickerModelCell(View view) {
      super(view);
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}

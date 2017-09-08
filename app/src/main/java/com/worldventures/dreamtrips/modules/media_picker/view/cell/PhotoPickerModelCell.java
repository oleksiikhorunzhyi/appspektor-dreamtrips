package com.worldventures.dreamtrips.modules.media_picker.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;

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

package com.worldventures.dreamtrips.modules.tripsimages.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_photo)
public class PhotoTimestampCell extends PhotoCell {

   @InjectView(R.id.photoTimestamp) TextView photoTimestampTextView;

   public PhotoTimestampCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      if (getModelObject() instanceof Photo) {
         Photo photo = (Photo) getModelObject();
         if (photo.getCreatedAt() != null) {
            photoTimestampTextView.setVisibility(View.VISIBLE);
            photoTimestampTextView.setText(DateTimeUtils
                  .getRelativeTimeSpanString(itemView.getResources(), photo.getCreatedAt().getTime()));
         } else {
            photoTimestampTextView.setVisibility(View.GONE);
         }
      }
   }
}

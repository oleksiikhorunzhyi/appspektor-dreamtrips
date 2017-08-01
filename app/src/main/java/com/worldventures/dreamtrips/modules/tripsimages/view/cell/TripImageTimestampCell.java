package com.worldventures.dreamtrips.modules.tripsimages.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_photo)
public class TripImageTimestampCell extends TripImageCell {

   @InjectView(R.id.photoTimestamp) TextView photoTimestampTextView;

   public TripImageTimestampCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      if (getModelObject().getCreatedAt() != null) {
         photoTimestampTextView.setVisibility(View.VISIBLE);
         photoTimestampTextView.setText(DateTimeUtils.getRelativeTimeSpanString(itemView.getResources(),
               getModelObject().getCreatedAt().getTime()));
      } else {
         photoTimestampTextView.setVisibility(View.GONE);
      }
   }
}

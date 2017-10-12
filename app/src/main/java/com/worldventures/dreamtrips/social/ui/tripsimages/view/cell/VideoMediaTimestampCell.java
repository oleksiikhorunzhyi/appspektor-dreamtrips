package com.worldventures.dreamtrips.social.ui.tripsimages.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_media_video)
public class VideoMediaTimestampCell extends VideoMediaCell {

   @InjectView(R.id.timestamp) TextView photoTimestampTextView;

   public VideoMediaTimestampCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      if (getModelObject().getItem().getCreatedAt() != null) {
         photoTimestampTextView.setVisibility(View.VISIBLE);
         photoTimestampTextView.setText(DateTimeUtils.getRelativeTimeSpanString(itemView.getResources(),
               getModelObject().getItem().getCreatedAt().getTime()));
      } else {
         photoTimestampTextView.setVisibility(View.GONE);
      }
   }
}

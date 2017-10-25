package com.worldventures.dreamtrips.social.ui.tripsimages.view.cell;

import android.view.View;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.utils.TimeUtils;

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
         photoTimestampTextView.setText(TimeUtils.getRelativeTimeSpanString(itemView.getResources(),
               getModelObject().getItem().getCreatedAt().getTime()));
      } else {
         photoTimestampTextView.setVisibility(View.GONE);
      }
   }
}

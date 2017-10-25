package com.worldventures.dreamtrips.modules.media_picker.view.cell;

import android.view.View;
import android.widget.TextView;

import com.worldventures.core.modules.picker.model.VideoPickerModel;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.utils.VideoDurationFormatter;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_video_pick)
public class VideoPickerModelCell extends MediaPickerModelCell<VideoPickerModel> {
   @InjectView(R.id.iv_duration) TextView durationTextView;

   public VideoPickerModelCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      durationTextView.setText(VideoDurationFormatter.getFormattedDuration(getModelObject().getDuration()));
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}

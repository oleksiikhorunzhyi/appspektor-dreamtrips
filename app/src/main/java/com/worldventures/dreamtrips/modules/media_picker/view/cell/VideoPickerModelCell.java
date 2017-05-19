package com.worldventures.dreamtrips.modules.media_picker.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.util.PickerVideoDurationFormatter;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_video_pick)
public class VideoPickerModelCell extends MediaPickerModelCell<VideoPickerModel> {
   @InjectView(R.id.iv_duration) TextView durationTextView;

   @Inject PickerVideoDurationFormatter pickerVideoDurationFormatter;

   public VideoPickerModelCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      durationTextView.setText(pickerVideoDurationFormatter.getFormattedDuration(getModelObject().getDuration()));
   }
}

package com.worldventures.dreamtrips.modules.video.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.adapter_video_header)
public class VideoHeaderLightCell extends VideoHeaderCell {

    public VideoHeaderLightCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        header.setTextColor(itemView.getResources().getColor(R.color.theme_main_darker));
    }

}

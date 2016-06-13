package com.worldventures.dreamtrips.modules.video.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.adapter_media_header)
public class MediaHeaderLightCell extends MediaHeaderCell {

    public MediaHeaderLightCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        header.setTextColor(itemView.getResources().getColor(R.color.theme_main_darker));
    }

}

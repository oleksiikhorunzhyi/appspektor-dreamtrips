package com.worldventures.dreamtrips.modules.video.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

@Layout(R.layout.adapter_video_header)
public class VideoHeaderCell extends AbstractCell<String> {

    @InjectView(R.id.header)
    TextView header;

    public VideoHeaderCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        header.setText(getModelObject());
        header.setTextColor(itemView.getResources().getColor(R.color.white));
    }

    @Override
    public void prepareForReuse() {

    }
}

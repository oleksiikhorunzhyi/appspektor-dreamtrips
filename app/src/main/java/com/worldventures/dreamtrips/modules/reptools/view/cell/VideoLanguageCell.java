package com.worldventures.dreamtrips.modules.reptools.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.modules.membership.event.VideoLanguageSelectedEvent;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLanguage;

import butterknife.InjectView;

@Layout(android.R.layout.simple_list_item_1)
public class VideoLanguageCell extends AbstractCell<VideoLanguage> {

    @InjectView(android.R.id.text1) TextView text;

    public VideoLanguageCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        text.setText(getModelObject().getTitle());
        itemView.setOnClickListener(view -> getEventBus().post(new VideoLanguageSelectedEvent(getModelObject())));
    }
}

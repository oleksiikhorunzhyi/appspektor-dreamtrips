package com.worldventures.dreamtrips.modules.reptools.view.cell;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.OnSuccessStoryCellClickEvent;
import com.worldventures.dreamtrips.core.utils.events.SuccessStoryItemSelectedEvent;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_success_story)
public class SuccessStoryCell extends AbstractCell<SuccessStory> {

    @InjectView(R.id.tv_title)
    protected TextView tvTitle;
    @InjectView(R.id.vg_parent)
    protected ViewGroup vgParent;

    public SuccessStoryCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }

        tvTitle.setText(getModelObject().getAuthor());
    }

    public void onEventMainThread(SuccessStoryItemSelectedEvent event) {
        if (getPosition() == event.getPosition()) {
            vgParent.setBackgroundColor(vgParent.getResources().getColor(R.color.selected_success_story));
        } else {
            vgParent.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public void prepareForReuse() {
        //nothing to do here
    }

    @OnClick(R.id.vg_parent)
    public void onItemClick() {
        getEventBus().post(new OnSuccessStoryCellClickEvent(getModelObject(), getPosition()));
    }

}

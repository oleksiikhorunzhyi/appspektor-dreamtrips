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

    @InjectView(R.id.tv_title) TextView tvTitle;
    @InjectView(R.id.vg_parent) ViewGroup vgParent;

    public SuccessStoryCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }

        updateSelection();
        tvTitle.setText(getModelObject().getAuthor());
    }

    public void onEventMainThread(SuccessStoryItemSelectedEvent event) {
        if (getPosition() == event.getPosition()) {
            getModelObject().setSelected(true);
        } else {
            getModelObject().setSelected(false);
        }
        updateSelection();
    }

    private void updateSelection() {
        if (getModelObject().isSelected()) {
            vgParent.setBackgroundColor(vgParent.getResources().getColor(R.color.grey_lighter));
        } else {
            vgParent.setBackgroundColor(Color.WHITE);
        }
    }

    @OnClick(R.id.vg_parent)
    public void onItemClick() {
        getEventBus().post(new OnSuccessStoryCellClickEvent(getModelObject(), getPosition()));
    }
}

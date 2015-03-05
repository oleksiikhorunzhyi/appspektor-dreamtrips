package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.SuccessStory;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_success_story)
public class SuccessStoryCell extends AbstractCell<SuccessStory> {
    public SuccessStoryCell(View view) {
        super(view);
    }

    @InjectView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void syncUIStateWithModel() {
        tvTitle.setText(getModelObject().getAuthor());
    }

    @Override
    public void prepareForReuse() {

    }
}

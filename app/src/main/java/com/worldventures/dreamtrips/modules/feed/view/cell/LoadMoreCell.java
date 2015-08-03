package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.event.LoadMoreEvent;
import com.worldventures.dreamtrips.modules.feed.model.comment.LoadMore;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_more_comments)
public class LoadMoreCell extends AbstractCell<LoadMore> {

    @InjectView(R.id.caption)
    TextView caption;

    public LoadMoreCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (getModelObject().isLoading()) {
            caption.setText(R.string.loading);
        } else {
            caption.setText(R.string.comment_view_more);
        }
        itemView.setOnClickListener(view -> getEventBus().post(new LoadMoreEvent()));
    }

    @Override
    public void prepareForReuse() {

    }
}

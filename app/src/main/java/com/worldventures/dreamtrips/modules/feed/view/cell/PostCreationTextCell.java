package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.PostDescription;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_post_text)
public class PostCreationTextCell extends AbstractDelegateCell<PostDescription, PostCreationTextCell.Delegate> {

    @InjectView(R.id.post)
    TextView post;

    public PostCreationTextCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        post.setText(getModelObject().getDescription());
        post.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
    }

    @Override
    public void clearResources() {
        super.clearResources();
        post.setOnFocusChangeListener(null);
    }

    @Override
    public void prepareForReuse() {

    }

    public interface Delegate extends CellDelegate<PostDescription> {

    }
}

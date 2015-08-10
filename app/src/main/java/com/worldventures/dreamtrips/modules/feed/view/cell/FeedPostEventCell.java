package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.FeedPostEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;

import butterknife.InjectView;

@Layout(R.layout.adapter_post_event)
public class FeedPostEventCell extends FeedHeaderCell<FeedPostEventModel> {

    @InjectView(R.id.post)
    TextView post;

    public FeedPostEventCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        FeedPostEventModel obj = getModelObject();
        post.setText(obj.getEntities()[0].getDescription());
    }


    @Override
    public void prepareForReuse() {

    }
}

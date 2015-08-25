package com.worldventures.dreamtrips.modules.feed.view.cell.base;

import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.event.CommentsPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.view.util.CommentCellHelper;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemHeaderHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public abstract class FeedHeaderCell<T extends BaseEventModel> extends AbstractCell<T> {

    private boolean syncUIStateWithModelWasCalled = false;

    FeedItemHeaderHelper feedItemHeaderHelper;
    CommentCellHelper commentCellHelper;

    @Optional
    @InjectView(R.id.comment_preview)
    View commentPreview;

    public FeedHeaderCell(View view) {
        super(view);
        feedItemHeaderHelper = new FeedItemHeaderHelper();
        ButterKnife.inject(feedItemHeaderHelper, view);

        if (commentPreview != null) {
            commentCellHelper = new CommentCellHelper();
            ButterKnife.inject(commentCellHelper, view);
        }
    }

    @Override
    protected void syncUIStateWithModel() {
        feedItemHeaderHelper.set(getModelObject(), itemView.getContext());

        if (commentCellHelper != null) {
            Comment comment = Queryable.from(getModelObject().getItem().getComments())
                    .firstOrDefault();
            if (comment != null) {
                commentPreview.setVisibility(View.VISIBLE);
                commentCellHelper.set(itemView.getContext(), comment);
            } else {
                commentPreview.setVisibility(View.GONE);
            }
        }

        syncUIStateWithModelWasCalled = true;
    }


    @Override
    public void fillWithItem(T item) {
        syncUIStateWithModelWasCalled = false;
        super.fillWithItem(item);
        if (!syncUIStateWithModelWasCalled) {
            throw new IllegalStateException("super.syncUIStateWithModel was not called");
        }

    }

    @Optional
    @OnClick({R.id.comments, R.id.comment_preview})
    void commentsClicked() {
        getEventBus().post(new CommentsPressedEvent(getModelObject()));
    }

    @Optional
    @OnClick(R.id.likes)
    void likeClicked() {
        getEventBus().post(new LikesPressedEvent(getModelObject()));
    }

}

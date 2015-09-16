package com.worldventures.dreamtrips.modules.feed.view.cell.base;

import android.os.Bundle;
import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.ProfileClickedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CommentsFragment;
import com.worldventures.dreamtrips.modules.feed.view.util.CommentCellHelper;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemHeaderHelper;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;

import javax.inject.Inject;

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
    @Optional
    @InjectView(R.id.comment_divider)
    View commentDivider;

    @Inject
    ActivityRouter activityRouter;

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
            Comment comment = getModelObject().getItem().getComments() == null ? null :
                    Queryable.from(getModelObject().getItem().getComments())
                            .firstOrDefault();
            if (comment != null) {
                commentDivider.setVisibility(View.VISIBLE);
                commentPreview.setVisibility(View.VISIBLE);
                commentCellHelper.set(itemView.getContext(), comment);
            } else {
                commentDivider.setVisibility(View.GONE);
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
    @OnClick({R.id.comment_preview, R.id.comments_count, R.id.likes_count})
    void commentsCountClicked() {
        openComments(getModelObject());
    }

    @Optional
    @OnClick(R.id.comments)
    void commentsClicked() {
        openComments(getModelObject(), true);
    }

    @Optional
    @OnClick(R.id.likes)
    void likeClicked() {
        getEventBus().post(new LikesPressedEvent(getModelObject()));
    }

    protected void openComments(BaseEventModel baseFeedModel) {
        openComments(baseFeedModel, false);
    }

    protected void openComments(BaseEventModel baseFeedModel, boolean openKeyboard) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CommentsFragment.EXTRA_FEED_ITEM, baseFeedModel);
        bundle.putBoolean(CommentsFragment.EXTRA_OPEN_COMMENT_KEYBOARD, openKeyboard);
        //
        NavigationBuilder.create()
                .with(activityRouter)
                .args(bundle)
                .move(Route.COMMENTS);
    }

    @Optional
    @OnClick(R.id.feed_header_avatar)
    void eventOwnerClicked() {
        User user = getModelObject().getLinks().getUsers().get(0);
        getEventBus().post(new ProfileClickedEvent(user));
    }

    @Optional
    @OnClick(R.id.user_who_liked)
    void onUsersLikedPressed() {
        NavigationBuilder.create()
                .with(activityRouter)
                .data(new UsersLikedEntityBundle(getModelObject().getItem().getUid()))
                .move(Route.USERS_LIKED_CONTENT);
    }

    @Optional
    @OnClick(R.id.user_photo)
    void commentOwnerClicked() {
        User user = commentCellHelper.getComment().getOwner();
        getEventBus().post(new ProfileClickedEvent(user));
    }

}

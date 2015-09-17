package com.worldventures.dreamtrips.modules.feed.view.cell.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.ImageView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditPostEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.ProfileClickedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
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
import cn.pedant.SweetAlert.SweetAlertDialog;

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
    @Optional
    @InjectView(R.id.more)
    ImageView more;

    @Inject
    ActivityRouter activityRouter;
    @Inject
    SessionHolder<UserSession> sessionHolder;

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

        // for now we support edit/delete post only for textual updates
        if (sessionHolder.get().get().getUser().equals(getModelObject().getItem().getUser()) &&
                getModelObject().getType().equals(BaseEventModel.Type.POST)) {
            more.setVisibility(View.VISIBLE);
        } else {
            more.setVisibility(View.GONE);
        }
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
    @OnClick(R.id.more)
    void onMoreClicked() {
        PopupMenu popup = new PopupMenu(itemView.getContext(), more);
        popup.inflate(R.menu.menu_post_edit);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    Dialog dialog = new SweetAlertDialog(itemView.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(itemView.getResources().getString(R.string.post_delete))
                            .setContentText(itemView.getResources().getString(R.string.post_delete_caption))
                            .setConfirmText(itemView.getResources().getString(R.string.post_delete_confirm))
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismissWithAnimation();
                                getEventBus().post(new DeletePostEvent(getModelObject()));
                            });
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    break;
                case R.id.action_edit:
                    if (getModelObject().getType().equals(BaseEventModel.Type.POST))
                        getEventBus().post(new EditPostEvent((TextualPost) getModelObject().getItem()));
                    break;
            }

            return true;
        });
        popup.show();
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

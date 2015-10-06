package com.worldventures.dreamtrips.modules.feed.view.cell.base;

import android.app.Dialog;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.event.ProfileClickedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.modules.feed.view.util.CommentCellHelper;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedActionPanelViewActionHandler;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemHeaderHelper;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import cn.pedant.SweetAlert.SweetAlertDialog;

public abstract class FeedHeaderCell<T extends FeedItem> extends AbstractCell<T> {

    private boolean syncUIStateWithModelWasCalled = false;

    FeedItemHeaderHelper feedItemHeaderHelper;
    CommentCellHelper commentCellHelper;

    @Optional
    @InjectView(R.id.comment_preview)
    View commentPreview;
    @Optional
    @InjectView(R.id.comment_divider)
    View commentDivider;
    @InjectView(R.id.actionView)
    FeedActionPanelView actionView;

    @Inject
    ActivityRouter activityRouter;
    @Inject
    SessionHolder<UserSession> sessionHolder;
    @Inject
    FeedActionPanelViewActionHandler feedActionHandler;

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

        //trip is not generated by user content, so edit button is unavailable
        actionView.setState(getModelObject(), isForeignItem(getModelObject()));

        feedActionHandler.init(actionView);
    }

    private boolean isForeignItem(FeedItem feedItem) {
        return feedItem.getItem().getUser() == null
                || sessionHolder.get().get().getUser().getId() == (feedItem.getItem().getUser().getId());
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
    @OnClick(R.id.comment_preview)
    void commentsCountClicked() {
        openComments(getModelObject());
    }

    protected void showMoreDialog(@MenuRes int menuRes, @StringRes int headerDelete, @StringRes int textDelete) {
        // temporally, while edit button doesn't exists
        PopupMenu popup = new PopupMenu(itemView.getContext(), commentPreview);
        popup.inflate(menuRes);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    Dialog dialog = new SweetAlertDialog(itemView.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(itemView.getResources().getString(headerDelete))
                            .setContentText(itemView.getResources().getString(textDelete))
                            .setConfirmText(itemView.getResources().getString(R.string.post_delete_confirm))
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismissWithAnimation();
                                onDelete();
                            });
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    break;
                case R.id.action_edit:
                    onEdit();
                    break;
            }

            return true;
        });
        popup.show();
    }

    protected abstract void onDelete();

    protected abstract void onMore();

    protected abstract void onEdit();

    protected void openComments(FeedItem baseFeedModel) {
        openComments(baseFeedModel, false);
    }

    protected void openComments(FeedItem baseFeedModel, boolean openKeyboard) {
        NavigationBuilder.create()
                .with(activityRouter)
                .data(new CommentsBundle(baseFeedModel, openKeyboard))
                .move(Route.COMMENTS);
    }

    @Optional
    @OnClick(R.id.feed_header_avatar)
    void eventOwnerClicked() {
        User user = getModelObject().getLinks().getUsers().get(0);
        getEventBus().post(new ProfileClickedEvent(user));
    }

    @Optional
    @OnClick(R.id.user_photo)
    void commentOwnerClicked() {
        User user = commentCellHelper.getComment().getOwner();
        getEventBus().post(new ProfileClickedEvent(user));
    }

}

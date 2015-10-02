package com.worldventures.dreamtrips.modules.feed.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.api.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.api.CreateCommentCommand;
import com.worldventures.dreamtrips.modules.feed.api.DeleteCommentCommand;
import com.worldventures.dreamtrips.modules.feed.api.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.api.FlagItemCommand;
import com.worldventures.dreamtrips.modules.feed.api.GetCommentsQuery;
import com.worldventures.dreamtrips.modules.feed.event.CommentChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeleteBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeleteCommentRequestEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditCommentRequestEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadMoreEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetFlagContentQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.ArrayList;
import java.util.List;

import icepick.State;


public class BaseCommentPresenter extends Presenter<BaseCommentPresenter.View> {
    private int page = 1;
    private int commentsCount = 0;

    @State
    FeedItem feedModel;
    @State
    FeedEntity feedEntity;
    @State
    String comment;

    private List<Flag> flagsList;

    public BaseCommentPresenter(FeedItem feedItem) {
        this.feedModel = feedItem;
        feedEntity = feedItem.getItem();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        loadComments();
        view.setComment(comment);
    }


    private void loadComments() {
        view.setLoading(true);
        doRequest(new GetCommentsQuery(feedEntity.getUid(), page), this::onCommentsLoaded);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void post() {
        doRequest(new CreateCommentCommand(feedEntity.getUid(), comment), this::onCommentPosted, spiceException -> {
            view.onPostError();
            BaseCommentPresenter.super.handleError(spiceException);
        });
    }

    public void onEvent(LoadMoreEvent event) {
        loadComments();
    }

    public void onEvent(DeleteCommentRequestEvent event) {
        doRequest(new DeleteCommentCommand(event.getComment().getUid()), jsonObject -> {
            view.removeComment(event.getComment());
            feedEntity.getComments().remove(event.getComment());
            feedEntity.setCommentsCount(feedEntity.getCommentsCount() - 1);
            eventBus.post(new FeedEntityCommentedEvent(feedEntity));
        });
    }

    public void onEvent(EditCommentRequestEvent event) {
        EditCommentPresenter editCommentPresenter = new EditCommentPresenter(feedModel, event.getComment());
        view.editComment(editCommentPresenter);
    }


    public void onEvent(CommentChangedEvent event) {
        view.updateComment(event.getComment());
        feedEntity.getComments().set(feedEntity.getComments().indexOf(event.getComment()), event.getComment());
        eventBus.post(new FeedEntityCommentedEvent(feedEntity));

    }

    public void onEvent(EditBucketEvent event) {
        BucketBundle bundle = new BucketBundle();
        bundle.setType(event.getType());
        bundle.setBucketItemUid(event.getUid());

        fragmentCompass.removeEdit();
        if (view.isTabletLandscape()) {
            fragmentCompass.disableBackStack();
            fragmentCompass.setContainerId(R.id.container_details_floating);
            fragmentCompass.showContainer();
            NavigationBuilder.create().with(fragmentCompass).data(bundle).attach(Route.BUCKET_EDIT);
        } else {
            bundle.setLock(true);
            NavigationBuilder.create().with(activityRouter).data(bundle).move(Route.BUCKET_EDIT);
        }
    }

    public void onEvent(DeletePostEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DeletePostCommand(event.getEntity().getItem().getUid()),
                    aVoid -> itemDeleted(event.getEntity()));
    }

    public void onEvent(DeletePhotoEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DeletePhotoCommand(event.getEntity().getItem().getUid()),
                    aVoid -> itemDeleted(event.getEntity()));
    }

    public void onEvent(DeleteBucketEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DeleteBucketItemCommand(event.getEventModel().getItem().getUid()),
                    aVoid -> itemDeleted(event.getEventModel()));

    }

    private void itemDeleted(FeedItem model) {
        eventBus.post(new FeedEntityDeletedEvent(model));
        fragmentCompass.pop();
    }

    public void onEvent(LoadFlagEvent event) {
        if (view.isVisibleOnScreen()) {
            if (flagsList == null) {
                doRequest(new GetFlagContentQuery(), flags -> {
                    flagsList = flags;
                    event.getCell().showFlagDialog(flagsList);
                });
            } else {
                event.getCell().showFlagDialog(flagsList);
            }
        }
    }

    public void onEvent(ItemFlaggedEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new FlagItemCommand(event.getEntity().getUid(), event.getNameOfReason()), aVoid -> {
        });
    }

    private void onCommentPosted(Comment comment) {
        view.addComment(comment);
        feedEntity.getComments().add(0, comment);
        feedEntity.setCommentsCount(feedEntity.getCommentsCount() + 1);
        eventBus.post(new FeedEntityCommentedEvent(feedEntity));
    }

    private void onCommentsLoaded(ArrayList<Comment> comments) {
        if (comments.size() > 0) {
            page++;
            commentsCount += comments.size();
            view.setLoading(false);
            view.addComments(comments);

            if (commentsCount == feedEntity.getCommentsCount()) view.hideViewMore();

        } else {
            view.hideViewMore();
        }
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.setLoading(false);
    }

    public interface View extends Presenter.View {
        void addComments(List<Comment> commentList);

        void addComment(Comment comment);

        void removeComment(Comment comment);

        void updateComment(Comment comment);

        void setComment(String comment);

        void setLoading(boolean loading);

        void editComment(EditCommentPresenter presenter);

        void hideViewMore();

        void onPostError();
    }
}

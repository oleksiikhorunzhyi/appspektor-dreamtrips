package com.worldventures.dreamtrips.modules.feed.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.api.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.api.CreateCommentCommand;
import com.worldventures.dreamtrips.modules.feed.api.DeleteCommentCommand;
import com.worldventures.dreamtrips.modules.feed.api.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.api.GetCommentsQuery;
import com.worldventures.dreamtrips.modules.feed.api.GetUsersLikedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.api.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.api.UnlikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.event.CommentChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeleteBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeleteCommentRequestEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditCommentRequestEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadMoreEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;

import java.util.ArrayList;
import java.util.List;

import icepick.Icicle;

public class BaseCommentPresenter extends Presenter<BaseCommentPresenter.View> {
    private int page = 1;
    private int commentsCount = 0;

    @Icicle
    BaseEventModel feedModel;
    @Icicle
    IFeedObject feedEntity;
    @Icicle
    String comment;

    public BaseCommentPresenter(BaseEventModel feedModel) {
        this.feedModel = feedModel;
        feedEntity = feedModel.getItem();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);

        view.setHeader(feedModel);
        loadComments();

        view.setComment(comment);

        preloadUsersWhoLiked();
    }

    private void preloadUsersWhoLiked() {
        doRequest(new GetUsersLikedEntityQuery(feedEntity.getUid(), 1, 1), this::onUserLoaded,
                spiceException -> {
                });
    }

    private void onUserLoaded(List<User> users) {
        if (users != null && !users.isEmpty()) {
            feedModel.getItem().setFirstUserLikedItem(users.get(0).getFullName());
            view.updateHeader(feedModel);
        }
    }

    private void loadComments() {
        view.setLoading(true);
        doRequest(new GetCommentsQuery(feedEntity.getUid(), page), this::onCommentsLoaded);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void onEvent(LikesPressedEvent event) {
        if (view.isVisibleOnScreen()) {
            BaseEventModel model = event.getModel();
            DreamTripsRequest command = model.getItem().isLiked() ?
                    new UnlikeEntityCommand(model.getItem().getUid()) :
                    new LikeEntityCommand(model.getItem().getUid());
            doRequest(command, element -> itemLiked());
        }
    }

    private void itemLiked() {
        feedEntity.setLiked(!feedEntity.isLiked());
        int currentCount = feedEntity.getLikesCount();
        currentCount = feedEntity.isLiked() ? currentCount + 1 : currentCount - 1;
        feedEntity.setLikesCount(currentCount);

        view.updateHeader(feedModel);
        eventBus.post(new FeedEntityCommentedEvent(feedEntity));

        if (feedEntity.getLikesCount() == 1 && feedEntity.isLiked()) {
            preloadUsersWhoLiked();
        }
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
        bundle.setBucketItemId(event.getUid());

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

    private void itemDeleted(BaseEventModel model) {
        eventBus.post(new FeedEntityDeletedEvent(model));
        fragmentCompass.pop();
    }


    public void onEvent(FeedEntityChangedEvent event) {
        if (event.getFeedEntity().equals(feedEntity)) {

            event.getFeedEntity().setLikesCount(feedEntity.getLikesCount());
            event.getFeedEntity().setCommentsCount(feedEntity.getCommentsCount());
            event.getFeedEntity().setUser(feedEntity.getUser());
            event.getFeedEntity().setLiked(feedEntity.isLiked());
            event.getFeedEntity().setComments(feedEntity.getComments());
            event.getFeedEntity().setFirstUserLikedItem(feedEntity.getFirstUserLikedItem());

            feedModel.setItem(event.getFeedEntity());
            feedEntity = feedModel.getItem();
            view.updateHeader(feedModel);
        }
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

        void setHeader(BaseEventModel header);

        void updateHeader(BaseEventModel eventModel);

        void editComment(EditCommentPresenter presenter);

        void hideViewMore();

        void onPostError();
    }
}

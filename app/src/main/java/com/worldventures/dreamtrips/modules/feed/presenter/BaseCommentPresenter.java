package com.worldventures.dreamtrips.modules.feed.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.UidItemDelegate;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.api.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.api.GetCommentsQuery;
import com.worldventures.dreamtrips.modules.feed.api.GetUsersLikedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.event.DeleteBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeleteCommentRequestEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditCommentRequestEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadMoreEvent;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;


public class BaseCommentPresenter<T extends BaseCommentPresenter.View> extends Presenter<T> {
    @State
    FeedEntity feedEntity;
    @State
    String comment;
    private int page = 1;
    private int commentsCount = 0;
    private UidItemDelegate uidItemDelegate;

    @Inject
    FeedEntityManager entityManager;

    public BaseCommentPresenter(FeedEntity feedEntity) {
        this.feedEntity = feedEntity;
        uidItemDelegate = new UidItemDelegate(this);
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        loadComments();
        loadLikes();
        view.setComment(comment);
    }

    @Override
    public void onInjected() {
        super.onInjected();
        entityManager.setDreamSpiceManager(dreamSpiceManager);
    }

    private void loadComments() {
        view.setLoading(true);
        doRequest(new GetCommentsQuery(feedEntity.getUid(), page), this::onCommentsLoaded);
    }

    private void loadLikes() {
        doRequest(new GetUsersLikedEntityQuery(feedEntity.getUid(), 1, 1), this::onLikersLoaded);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void post() {
        entityManager.createComment(feedEntity, comment);
    }


    public void onEvent(FeedEntityManager.CommentEvent event) {
        switch (event.getType()) {
            case ADDED:
                if (event.getSpiceException() == null) {
                    view.addComment(event.getComment());
                    feedEntity.getComments().add(0, event.getComment());
                    feedEntity.setCommentsCount(feedEntity.getCommentsCount() + 1);
                    sendAnalytic(TrackingHelper.ATTRIBUTE_COMMENT);
                } else {
                    view.onPostError();
                    handleError(event.getSpiceException());
                }
                break;
            case REMOVED:
                view.removeComment(event.getComment());
                feedEntity.getComments().remove(event.getComment());
                feedEntity.setCommentsCount(feedEntity.getCommentsCount() - 1);
                sendAnalytic(TrackingHelper.ATTRIBUTE_DELETE_COMMENT);
                break;
            case EDITED:
                view.updateComment(event.getComment());
                feedEntity.getComments().set(feedEntity.getComments().indexOf(event.getComment()), event.getComment());
                break;

        }
        eventBus.post(new FeedEntityCommentedEvent(feedEntity));

    }

    public void onEvent(LoadMoreEvent event) {
        loadComments();
    }

    public void onEvent(DeleteCommentRequestEvent event) {
        if (!view.isVisibleOnScreen()) return;
        entityManager.deleteComment(event.getComment());
    }


    public void onEvent(EditCommentRequestEvent event) {
        view.editComment(event.getComment());
        sendAnalytic(TrackingHelper.ATTRIBUTE_EDIT_COMMENT);
    }

    public void onEvent(EditBucketEvent event) {
        BucketBundle bundle = new BucketBundle();
        bundle.setType(event.getType());
        bundle.setBucketItemUid(event.getUid());

        if (view.isTabletLandscape()) {
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
            doRequest(new DeletePostCommand(event.getEntity().getUid()),
                    aVoid -> itemDeleted(event.getEntity()));
    }

    public void onEvent(DeletePhotoEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DeletePhotoCommand(event.getEntity().getUid()),
                    aVoid -> itemDeleted(event.getEntity()));
    }

    public void onEvent(DeleteBucketEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DeleteBucketItemCommand(event.getEntity().getUid()),
                    aVoid -> itemDeleted(event.getEntity()));

    }

    private void itemDeleted(FeedEntity model) {
        eventBus.post(new FeedEntityDeletedEvent(model));
        fragmentCompass.pop();
    }

    public void onEvent(LoadFlagEvent event) {
        if (view.isVisibleOnScreen())
            uidItemDelegate.loadFlags(event.getFlaggableView());
    }

    public void onEvent(ItemFlaggedEvent event) {
        if (view.isVisibleOnScreen())
            uidItemDelegate.flagItem(new FlagData(event.getEntity().getUid(),
                    event.getFlagReasonId(), event.getNameOfReason()));
    }

    private void sendAnalytic(String actionAttribute) {
        String id = feedEntity.getUid();
        FeedEntityHolder.Type type = FeedEntityHolder.Type.UNDEFINED;
        if (feedEntity instanceof BucketItem) {
            type = FeedEntityHolder.Type.BUCKET_LIST_ITEM;
        }
        if (feedEntity instanceof Photo) {
            type = FeedEntityHolder.Type.PHOTO;
        }
        if (feedEntity instanceof TextualPost) {
            type = FeedEntityHolder.Type.POST;
        }
        if (feedEntity instanceof TripModel) {
            type = FeedEntityHolder.Type.TRIP;
        }

        if (type != FeedEntityHolder.Type.UNDEFINED) {
            eventBus.post(new FeedItemAnalyticEvent(actionAttribute, id, type));
        }
    }

    private void onCommentsLoaded(ArrayList<Comment> comments) {
        if (comments.size() > 0) {
            page++;
            commentsCount += comments.size();
            view.setLoading(false);
            feedEntity.getComments().addAll(comments);
            view.addComments(comments);
            if (commentsCount >= feedEntity.getCommentsCount()) view.hideViewMore();
            else view.showViewMore();

        } else {
            view.hideViewMore();
        }
    }

    private void onLikersLoaded(List<User> users) {
        if (users != null && !users.isEmpty()) {
            feedEntity.setFirstLikerName(users.get(0).getFullName());
        } else {
            feedEntity.setFirstLikerName(null);
        }
        eventBus.post(new FeedEntityChangedEvent(feedEntity));
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

        void editComment(Comment comment);

        void hideViewMore();

        void onPostError();

        void showViewMore();
    }
}

package com.worldventures.dreamtrips.modules.feed.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.DeleteItemHttpAction;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
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
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class BaseCommentPresenter<T extends BaseCommentPresenter.View> extends Presenter<T> {
    @Inject
    FeedEntityManager entityManager;

    @Inject
    BucketInteractor bucketInteractor;
    //

    @State
    FeedEntity feedEntity;
    @State
    String draftComment;
    private int page = 1;
    private int commentsCount = 0;
    private boolean loadInitiated;

    public BaseCommentPresenter(FeedEntity feedEntity) {
        this.feedEntity = feedEntity;
    }

    @Override
    public void onInjected() {
        super.onInjected();

        entityManager.setRequestingPresenter(this);
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        view.setDraftComment(draftComment);
        view.setLikePanel(feedEntity);
        //
        if (isNeedCheckCommentsWhenStart())
            checkCommentsAndLikesToLoad();
    }

    /**
     * Request comments and likes only once per view loading if suitable count > 0
     */
    protected void checkCommentsAndLikesToLoad() {
        if (loadInitiated) return;
        //
        if (feedEntity.getCommentsCount() > 0) {
            loadComments();
            loadInitiated = true;
        }
        if (feedEntity.getLikesCount() > 0) {
            loadLikes();
            loadInitiated = true;
        }
    }

    protected boolean isNeedCheckCommentsWhenStart() {
        return true;
    }

    private void loadComments() {
        view.setLoading(true);
        doRequest(new GetCommentsQuery(feedEntity.getUid(), page), this::onCommentsLoaded);
    }

    private void loadLikes() {
        doRequest(new GetUsersLikedEntityQuery(feedEntity.getUid(), 1, 2), this::onLikersLoaded);
    }

    public void setDraftComment(String comment) {
        this.draftComment = comment;
    }

    public void post() {
        entityManager.createComment(feedEntity, draftComment);
    }


    public void onEvent(FeedEntityManager.CommentEvent event) {
        switch (event.getType()) {
            case ADDED:
                if (event.getSpiceException() == null) {
                    view.addComment(event.getComment());
                    sendAnalytic(TrackingHelper.ATTRIBUTE_COMMENT);
                } else {
                    view.onPostError();
                    handleError(event.getSpiceException());
                }
                break;
            case REMOVED:
                view.removeComment(event.getComment());
                sendAnalytic(TrackingHelper.ATTRIBUTE_DELETE_COMMENT);
                break;
            case EDITED:
                view.updateComment(event.getComment());
                break;

        }
        eventBus.post(new FeedEntityCommentedEvent(feedEntity));
    }

    public void onEvent(LoadMoreEvent event) {
        loadComments();
    }

    public void onEvent(DeleteCommentRequestEvent event) {
        if (!view.isVisibleOnScreen()) return;
        entityManager.deleteComment(feedEntity, event.getComment());
    }


    public void onEvent(EditCommentRequestEvent event) {
        view.editComment(feedEntity, event.getComment());
        sendAnalytic(TrackingHelper.ATTRIBUTE_EDIT_COMMENT);
    }

    public void onEvent(EditBucketEvent event) {
        if (!view.isVisibleOnScreen()) return;
        //
        BucketBundle bundle = new BucketBundle();
        bundle.setType(event.type());
        bundle.setBucketItem(event.bucketItem());

        view.showEdit(bundle);
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
        if (view.isVisibleOnScreen()) {
            BucketItem bucketItemToDelete = event.getEntity();

            view.bind(bucketInteractor.deleteItemPipe()
                    .createObservable(new DeleteItemHttpAction(bucketItemToDelete.getUid()))
                    .observeOn(AndroidSchedulers.mainThread()))
                    .subscribe(new ActionStateSubscriber<DeleteItemHttpAction>()
                            .onSuccess(deleteItemAction -> itemDeleted(bucketItemToDelete))
                            .onFail((deleteItemAction, throwable) -> {
                                view.setLoading(false); //TODO: review, after leave from robospice completely
                                handleError(throwable);
                            }));
        }
    }

    protected void itemDeleted(FeedEntity model) {
        eventBus.post(new FeedEntityDeletedEvent(model));
        //
        back();
    }

    protected void back() {
        view.back();
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
            TrackingHelper.sendActionItemFeed(actionAttribute, id, type);
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
            User userWhoLiked = Queryable.from(users).firstOrDefault(user -> user.getId() != getAccount().getId());
            feedEntity.setFirstLikerName(userWhoLiked != null ? userWhoLiked.getFullName() : null);
        } else {
            feedEntity.setFirstLikerName(null);
        }
        view.setLikePanel(feedEntity);
        eventBus.post(new FeedEntityChangedEvent(feedEntity));
    }

    public interface View extends RxView {

        void addComments(List<Comment> commentList);

        void addComment(Comment comment);

        void removeComment(Comment comment);

        void updateComment(Comment comment);

        void setDraftComment(String comment);

        void setLoading(boolean loading);

        void editComment(FeedEntity feedEntity, Comment comment);

        void hideViewMore();

        void onPostError();

        void showViewMore();

        void showEdit(BucketBundle bucketBundle);

        void setLikePanel(FeedEntity entity);

        void back();
    }
}
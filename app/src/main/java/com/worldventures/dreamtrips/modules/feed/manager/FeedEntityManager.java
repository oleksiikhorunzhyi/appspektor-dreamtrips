package com.worldventures.dreamtrips.modules.feed.manager;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;
import com.worldventures.dreamtrips.modules.feed.api.CreateCommentCommand;
import com.worldventures.dreamtrips.modules.feed.api.DeleteCommentCommand;
import com.worldventures.dreamtrips.modules.feed.api.EditCommentCommand;
import com.worldventures.dreamtrips.modules.feed.api.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.api.UnlikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class FeedEntityManager {

    EventBus eventBus;
    RequestingPresenter requestingPresenter;
    List<String> uidsInLikeRequest = new ArrayList<>();

    public FeedEntityManager(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setRequestingPresenter(RequestingPresenter requestingPresenter) {
        this.requestingPresenter = requestingPresenter;
    }

    public void like(FeedEntity feedEntity) {
        if (!uidsInLikeRequest.contains(feedEntity.getUid())) {
            uidsInLikeRequest.add(feedEntity.getUid());
            LikeEntityCommand command = new LikeEntityCommand(feedEntity.getUid());
            requestingPresenter.doRequest(command, aVoid -> {
                actualizeLikes(feedEntity, true);
                eventBus.post(new EntityLikedEvent(feedEntity));
                uidsInLikeRequest.remove(feedEntity.getUid());
            }, spiceException -> {
                requestingPresenter.handleError(spiceException);
                Timber.e(spiceException, this.getClass().getSimpleName());
                uidsInLikeRequest.remove(feedEntity.getUid());
            });
        }
    }

    public void unlike(FeedEntity feedEntity) {
        if (!uidsInLikeRequest.contains(feedEntity.getUid())) {
            uidsInLikeRequest.add(feedEntity.getUid());
            UnlikeEntityCommand command = new UnlikeEntityCommand(feedEntity.getUid());
            requestingPresenter.doRequest(command, aVoid -> {
                actualizeLikes(feedEntity, false);
                eventBus.post(new EntityLikedEvent(feedEntity));
                uidsInLikeRequest.remove(feedEntity.getUid());
            }, spiceException -> {
                requestingPresenter.handleError(spiceException);
                Timber.e(spiceException, this.getClass().getSimpleName());
                uidsInLikeRequest.remove(feedEntity.getUid());
            });
        }
    }


    public void createComment(FeedEntity feedEntity, String comment) {
        requestingPresenter.doRequest(new CreateCommentCommand(feedEntity.getUid(), comment),
                comment1 -> {
                    feedEntity.setCommentsCount(feedEntity.getCommentsCount() + 1);
                    feedEntity.getComments().add(comment1);

                    eventBus.post(new CommentEvent(comment1, CommentEvent.Type.ADDED));
                },
                spiceException -> handelCommentError(spiceException, CommentEvent.Type.ADDED));
    }

    public void deleteComment(FeedEntity feedEntity, Comment comment) {
        requestingPresenter.doRequest(new DeleteCommentCommand(comment.getUid()), jsonObject -> {
            feedEntity.setCommentsCount(feedEntity.getCommentsCount() - 1);
            feedEntity.getComments().remove(comment);

            eventBus.post(new CommentEvent(comment, CommentEvent.Type.REMOVED));
        }, spiceException -> handelCommentError(spiceException, CommentEvent.Type.REMOVED));
    }

    public void updateComment(FeedEntity feedEntity, Comment comment) {
        requestingPresenter.doRequest(new EditCommentCommand(comment), result -> {
            feedEntity.getComments().set(feedEntity.getComments().indexOf(result), result);

            eventBus.post(new CommentEvent(comment, CommentEvent.Type.EDITED));
        }, spiceException -> handelCommentError(spiceException, CommentEvent.Type.EDITED));
    }

    private void handelCommentError(SpiceException spiceException, CommentEvent.Type type) {
        requestingPresenter.handleError(spiceException);
        CommentEvent event = new CommentEvent(null, type);
        event.setSpiceException(spiceException);
        eventBus.post(event);
    }

    private void actualizeLikes(FeedEntity feedEntity, boolean liked) {
        feedEntity.setLiked(liked);
        int currentCount = feedEntity.getLikesCount();
        currentCount = feedEntity.isLiked() ? currentCount + 1 : currentCount - 1;
        feedEntity.setLikesCount(currentCount);
    }


    public static class CommentEvent {
        Comment comment;
        Type type;
        SpiceException spiceException;

        public CommentEvent(Comment comment, Type type) {
            this.comment = comment;
            this.type = type;
        }

        public Comment getComment() {
            return comment;
        }

        public Type getType() {
            return type;
        }

        public SpiceException getSpiceException() {
            return spiceException;
        }

        public void setSpiceException(SpiceException spiceException) {
            this.spiceException = spiceException;
        }

        public enum Type {
            ADDED, REMOVED, EDITED;
        }

    }
}

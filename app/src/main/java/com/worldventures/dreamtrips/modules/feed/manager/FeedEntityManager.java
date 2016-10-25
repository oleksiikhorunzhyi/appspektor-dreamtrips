package com.worldventures.dreamtrips.modules.feed.manager;

import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FeedEntityManagerListener;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.LikesInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.CreateCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeleteCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.UnlikeEntityCommand;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class FeedEntityManager {

   private EventBus eventBus;
   private FeedEntityManagerListener feedEntityManagerListener;
   private List<String> uidsWithPendingLikes = new ArrayList<>();

   private LikesInteractor likesInteractor;
   private CommentsInteractor commentsInteractor;

   public FeedEntityManager(EventBus eventBus, LikesInteractor likesInteractor, CommentsInteractor commentsInteractor) {
      this.eventBus = eventBus;
      this.likesInteractor = likesInteractor;
      this.commentsInteractor = commentsInteractor;
   }

   public void setFeedEntityManagerListener(FeedEntityManagerListener feedEntityManagerListener) {
      this.feedEntityManagerListener = feedEntityManagerListener;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Likes
   ///////////////////////////////////////////////////////////////////////////

   public void like(FeedEntity feedEntity) {
      if (uidsWithPendingLikes.contains(feedEntity.getUid())) {
         return;
      }
      uidsWithPendingLikes.add(feedEntity.getUid());
      likesInteractor.likePipe()
            .createObservable(new LikeEntityCommand(feedEntity.getUid()))
            .compose(new IoToMainComposer<>())
            .subscribe(new ActionStateSubscriber<LikeEntityCommand>()
                  .onSuccess(likeEntityCommand -> {
                     actualizeLikes(feedEntity, true);
                     eventBus.post(new EntityLikedEvent(feedEntity));
                     uidsWithPendingLikes.remove(feedEntity.getUid());
                  })
                  .onFail((command, throwable) -> handleLikeCommandError(feedEntity, command, throwable))
            );
   }

   public void unlike(FeedEntity feedEntity) {
      if (uidsWithPendingLikes.contains(feedEntity.getUid())) {
         return;
      }
      uidsWithPendingLikes.add(feedEntity.getUid());

      likesInteractor.unlikePipe()
            .createObservable(new UnlikeEntityCommand(feedEntity.getUid()))
            .compose(new IoToMainComposer<>())
            .subscribe(new ActionStateSubscriber<UnlikeEntityCommand>()
                  .onSuccess(command -> {
                     actualizeLikes(feedEntity, false);
                     eventBus.post(new EntityLikedEvent(feedEntity));
                     uidsWithPendingLikes.remove(feedEntity.getUid());
                  })
                  .onFail((command, throwable) -> handleLikeCommandError(feedEntity, command, throwable))
            );
   }

   private void actualizeLikes(FeedEntity feedEntity, boolean liked) {
      feedEntity.setLiked(liked);
      int currentCount = feedEntity.getLikesCount();
      currentCount = feedEntity.isLiked() ? currentCount + 1 : currentCount - 1;
      feedEntity.setLikesCount(currentCount);
   }
   
   private void handleLikeCommandError(FeedEntity feedEntity, Command command, Throwable throwable) {
      feedEntityManagerListener.handleError(command, throwable);
      Timber.e(throwable, this.getClass().getSimpleName());
      uidsWithPendingLikes.remove(feedEntity.getUid());
   }

   ///////////////////////////////////////////////////////////////////////////
   // Comments
   ///////////////////////////////////////////////////////////////////////////

   public void createComment(FeedEntity feedEntity, String commentText) {
      commentsInteractor.createCommentPipe()
            .createObservable(new CreateCommentCommand(feedEntity.getUid(), commentText))
            .compose(new IoToMainComposer<>())
            .subscribe(new ActionStateSubscriber<CreateCommentCommand>()
               .onSuccess(command -> {
                  Comment comment = command.getResult();
                  feedEntity.setCommentsCount(feedEntity.getCommentsCount() + 1);
                  feedEntity.getComments().add(comment);
                  eventBus.post(new CommentEvent(comment, CommentEvent.Type.ADDED));
               })
               .onFail((command, throwable) -> handleCommentError(command, throwable, CommentEvent.Type.ADDED)));
   }

   public void deleteComment(FeedEntity feedEntity, Comment comment) {
      commentsInteractor.deleteCommentPipe()
            .createObservable(new DeleteCommentCommand(comment.getUid()))
            .compose(new IoToMainComposer<>())
            .subscribe(new ActionStateSubscriber<DeleteCommentCommand>()
                  .onSuccess(command -> {
                     feedEntity.setCommentsCount(feedEntity.getCommentsCount() - 1);
                     feedEntity.getComments().remove(comment);
                     eventBus.post(new CommentEvent(comment, CommentEvent.Type.REMOVED));
                  })
                  .onFail((failedCommand, throwable)
                        -> handleCommentError(failedCommand, throwable, CommentEvent.Type.REMOVED)));
   }

   public void updateComment(FeedEntity feedEntity, Comment comment) {
      commentsInteractor.editCommentPipe()
            .createObservable(new EditCommentCommand(comment.getUid(), comment.getMessage()))
            .compose(new IoToMainComposer<>())
            .subscribe(new ActionStateSubscriber<EditCommentCommand>()
                  .onSuccess(command -> {
                     Comment updatedComment = command.getResult();
                     int location = feedEntity.getComments().indexOf(updatedComment);
                     if (location != -1) {
                        feedEntity.getComments().set(location, updatedComment);
                        eventBus.post(new CommentEvent(comment, CommentEvent.Type.EDITED));
                     }
                  })
                  .onFail((failedCommand, throwable)
                        -> handleCommentError(failedCommand, throwable, CommentEvent.Type.EDITED)));
   }

   private void handleCommentError(Command command, Throwable exception, CommentEvent.Type type) {
      feedEntityManagerListener.handleError(command, exception);

      CommentEvent event = new CommentEvent(null, type);
      event.setException(exception);
      eventBus.post(event);
   }

   public static class CommentEvent {
      Comment comment;
      Type type;
      Throwable exception;

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

      public Throwable getException() {
         return exception;
      }

      public void setException(Throwable exception) {
         this.exception = exception;
      }

      public enum Type {
         ADDED, REMOVED, EDITED;
      }

   }
}

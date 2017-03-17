package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FlagDelegate;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadMoreEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.modules.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.CreateCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeleteCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetCommentsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.friends.service.FriendsInteractor;
import com.worldventures.dreamtrips.modules.friends.service.command.GetLikersCommand;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;

import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

public class BaseCommentPresenter<T extends BaseCommentPresenter.View> extends Presenter<T>
      implements FeedActionHandlerPresenter {

   private static final int PAGE = 1;
   private static final int PER_PAGE = 2;

   @Inject BucketInteractor bucketInteractor;
   @Inject TranslationFeedInteractor translationFeedInteractor;
   @Inject CommentsInteractor commentsInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject FriendsInteractor friendsInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;

   @State FeedEntity feedEntity;
   @State String draftCommentText;

   private int page = 1;
   private int commentsCount = 0;
   private boolean loadInitiated;

   public BaseCommentPresenter(FeedEntity feedEntity) {
      this.feedEntity = feedEntity;
   }

   @Override
   public void takeView(T view) {
      super.takeView(view);
      view.setDraftComment(draftCommentText);
      view.setLikePanel(feedEntity);

      if (isNeedCheckCommentsWhenStart()) checkCommentsAndLikesToLoad();

      subscribeToCommentDeletion();
      subscribeToCommentCreation();
      subscribeToCommentChanges();
      subscribeToCommentsLoading();
      subscribeToCommentTranslation();
   }

   private void subscribeToCommentsLoading() {
      view.bindUntilDropView(commentsInteractor.commentsPipe().observe().compose(new IoToMainComposer<>()))
            .subscribe(new ActionStateSubscriber<GetCommentsCommand>()
                  .onSuccess(getCommentsCommand -> onCommentsLoaded(getCommentsCommand.getResult()))
                  .onFail(this::handleError));
   }

   private void subscribeToCommentTranslation() {
      translationFeedInteractor.translateCommentPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<TranslateUidItemCommand.TranslateCommentCommand>()
                  .onSuccess(translateCommentCommand -> {
                     updateEntityComments(translateCommentCommand.getResult());
                     view.updateComment(translateCommentCommand.getResult());
                  }).onFail((translateCommentCommand, throwable) -> {
                     view.notifyDataSetChanged();
                     handleError(translateCommentCommand, throwable);
                  }));
   }

   protected void checkCommentsAndLikesToLoad() {
      if (loadInitiated) return;
      //
      loadComments();
      loadInitiated = true;
      loadFirstLikers();
   }

   protected boolean isNeedCheckCommentsWhenStart() {
      return true;
   }

   private void loadComments() {
      view.setLoading(true);
      commentsInteractor.commentsPipe().send(new GetCommentsCommand(feedEntity.getUid(), page));
   }

   private void onCommentsLoaded(List<Comment> comments) {
      if (comments.size() > 0) {
         page++;
         commentsCount += comments.size();
         view.setLoading(false);
         feedEntity.getComments().addAll(comments);
         view.addComments(comments);
         if (commentsCount >= feedEntity.getCommentsCount()) {
            view.hideViewMore();
         } else {
            view.showViewMore();
         }
      } else {
         view.hideViewMore();
      }
   }

   private void loadFirstLikers() {
      friendsInteractor.getLikersPipe()
            .createObservable(new GetLikersCommand(feedEntity, PAGE, PER_PAGE))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetLikersCommand>()
                  .onSuccess(likersCommand -> {
                     this.feedEntity = likersCommand.getFeedEntity();
                     view.setLikePanel(feedEntity);
                  })
                  .onFail(this::handleError));
   }

   public void setDraftCommentText(String commentText) {
      this.draftCommentText = commentText;
   }

   @Override
   public void onLikeItem(FeedItem feedItem) {}

   @Override
   public void onDownloadImage(String url) {}

   @Override
   public void onTranslateFeedEntity(FeedEntity translatableItem) {}

   @Override
   public void onShowOriginal(FeedEntity translatableItem) {}

   @Override
   public void onCommentItem(FeedItem feedItem) {
      view.openInput();
   }

   @Override
   public void onLoadFlags(Flaggable flaggableView) {
      feedActionHandlerDelegate.onLoadFlags(flaggableView, this::handleError);
   }

   @Override
   public void onFlagItem(FeedItem feedItem, int flagReasonId, String reason) {
      flag(feedItem.getItem().getUid(), flagReasonId, reason);
   }

   public void onFlagComment(String uid, int flagReasonId, String reason) {
      flag(uid, flagReasonId, reason);
   }

   private void flag(String id, int flagReasonId, String reason) {
      feedActionHandlerDelegate.onFlagItem(id, flagReasonId, reason, view, this::handleError);
   }

   public void editComment(Comment comment) {
      view.editComment(feedEntity, comment);
      sendAnalytic(TrackingHelper.ATTRIBUTE_EDIT_COMMENT);
   }

   public void translateComment(Comment comment) {
      translationFeedInteractor.translateCommentPipe()
            .send(new TranslateUidItemCommand.TranslateCommentCommand(comment, LocaleHelper.getDefaultLocaleFormatted()));
   }

   public void deleteComment(Comment comment) {
      commentsInteractor.deleteCommentPipe().send(new DeleteCommentCommand(feedEntity, comment));
   }

   private void subscribeToCommentDeletion() {
      commentsInteractor.deleteCommentPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<DeleteCommentCommand>()
                  .onSuccess(this::commentDeleted)
                  .onFail(this::handleError));
   }

   private void commentDeleted(DeleteCommentCommand deleteCommentCommand) {
      view.removeComment(deleteCommentCommand.getResult());
      sendAnalytic(TrackingHelper.ATTRIBUTE_DELETE_COMMENT);
      eventBus.post(new FeedEntityCommentedEvent(deleteCommentCommand.getFeedEntity()));
   }

   public void createComment() {
      commentsInteractor.createCommentPipe().send(new CreateCommentCommand(feedEntity, draftCommentText));
   }

   private void subscribeToCommentCreation() {
      commentsInteractor.createCommentPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CreateCommentCommand>()
                  .onSuccess(this::commentCreated)
                  .onFail(this::comentCreationError));
   }

   private void commentCreated(CreateCommentCommand createCommentCommand) {
      view.addComment(createCommentCommand.getResult());
      sendAnalytic(TrackingHelper.ATTRIBUTE_COMMENT);
      eventBus.post(new FeedEntityCommentedEvent(createCommentCommand.getFeedEntity()));
   }

   private void comentCreationError(CreateCommentCommand createCommentCommand, Throwable e) {
      view.onPostError();
      handleError(createCommentCommand, e);
   }

   private void subscribeToCommentChanges() {
      commentsInteractor.editCommentPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<EditCommentCommand>()
                  .onSuccess(this::commentEdited));
   }

   private void commentEdited(EditCommentCommand commentCommand) {
      view.updateComment(commentCommand.getResult());
      eventBus.post(new FeedEntityCommentedEvent(commentCommand.getFeedEntity()));
   }

   public void onEvent(LoadMoreEvent event) {
      loadComments();
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

   private void updateEntityComments(Comment comment) {
      int commentIndex = feedEntity.getComments().indexOf(comment);
      if (commentIndex != -1) feedEntity.getComments().set(commentIndex, comment);
   }

   public interface View extends RxView, FlagDelegate.View, ApiErrorView {

      void addComments(List<Comment> commentList);

      void addComment(Comment comment);

      void removeComment(Comment comment);

      void updateComment(Comment comment);

      void setDraftComment(String comment);

      void openInput();

      void setLoading(boolean loading);

      void notifyDataSetChanged();

      void editComment(FeedEntity feedEntity, Comment comment);

      void hideViewMore();

      void onPostError();

      void showViewMore();

      void setLikePanel(FeedEntity entity);

      void back();
   }
}

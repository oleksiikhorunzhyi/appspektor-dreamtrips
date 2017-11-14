package com.worldventures.dreamtrips.social.ui.feed.presenter;

import android.text.TextUtils;

import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.CommentAction;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.comment.Comment;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.social.ui.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreateCommentCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.DeleteCommentCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.EditCommentCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetCommentsCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.TranslateUidItemCommand;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.social.ui.flags.service.FlagDelegate;
import com.worldventures.dreamtrips.social.ui.friends.service.FriendsInteractor;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetLikersCommand;

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
   @Inject FriendsInteractor friendsInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;

   @State FeedEntity feedEntity;

   private int page = 1;
   private boolean loadInitiated;

   public BaseCommentPresenter(FeedEntity feedEntity) {
      this.feedEntity = feedEntity;
   }

   @Override
   public void takeView(T view) {
      super.takeView(view);
      view.setLikePanel(feedEntity);

      if (isNeedCheckCommentsWhenStart()) {
         checkCommentsAndLikesToLoad();
      }

      subscribeToCommentDeletion();
      subscribeToCommentCreation();
      subscribeToCommentChanges();
      subscribeToCommentTranslation();
   }

   @Override
   public void onResume() {
      super.onResume();
      commentsInteractor.commentsPipe()
            .observe()
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe(new ActionStateSubscriber<GetCommentsCommand>()
                  .onSuccess(this::onCommentsLoaded)
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
      if (loadInitiated) {
         return;
      }
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
      commentsInteractor.commentsPipe().send(new GetCommentsCommand(feedEntity, page));
   }

   private void onCommentsLoaded(GetCommentsCommand getCommentsCommand) {
      this.feedEntity = getCommentsCommand.getFeedEntity();
      List<Comment> newComments = getCommentsCommand.getResult();
      if (newComments.isEmpty()) {
         view.hideViewMore();
         return;
      }
      page++;
      view.setLoading(false);
      view.addComments(newComments);
      if (feedEntity.getComments().size() >= feedEntity.getCommentsCount()) {
         view.hideViewMore();
      } else {
         view.showViewMore();
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

   @Override
   public void onLikeItem(FeedItem feedItem) {
      //do nothing
   }

   @Override
   public void onDownloadImage(String url) {
      //do nothing
   }

   @Override
   public void onTranslateFeedEntity(FeedEntity translatableItem) {
      //do nothing
   }

   @Override
   public void onShowOriginal(FeedEntity translatableItem) {
      //do nothing
   }

   @Override
   public void onCommentItem(FeedItem feedItem) {
      view.openInput();
   }

   public void onCommentTextChanged(String comment) {
      view.enablePostButton(!TextUtils.isEmpty(comment));
   }

   @Override
   public void onLoadFlags(Flaggable flaggableView) {
      feedActionHandlerDelegate.onLoadFlags(flaggableView, this::handleError);
   }

   @Override
   public void onFlagItem(String uid, int flagReasonId, String reason) {
      flag(uid, flagReasonId, reason);
   }

   public void onFlagComment(String uid, int flagReasonId, String reason) {
      flag(uid, flagReasonId, reason);
   }

   private void flag(String id, int flagReasonId, String reason) {
      feedActionHandlerDelegate.onFlagItem(id, flagReasonId, reason, view, this::handleError);
   }

   public void editComment(Comment comment) {
      view.editComment(feedEntity, comment);
      analyticsInteractor.analyticsActionPipe().send(CommentAction.edit(feedEntity));
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
      analyticsInteractor.analyticsActionPipe().send(CommentAction.delete(feedEntity));
   }

   public void createComment(String comment) {
      commentsInteractor.createCommentPipe().send(new CreateCommentCommand(feedEntity, comment));
   }

   private void subscribeToCommentCreation() {
      commentsInteractor.createCommentPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CreateCommentCommand>()
                  .onSuccess(this::commentCreated)
                  .onFail(this::commentCreationError));
   }

   private void commentCreated(CreateCommentCommand createCommentCommand) {
      view.addComment(createCommentCommand.getResult());
      analyticsInteractor.analyticsActionPipe().send(CommentAction.add(feedEntity));
   }

   private void commentCreationError(CreateCommentCommand createCommentCommand, Throwable e) {
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
   }

   public void onLoadMoreComments() {
      loadComments();
   }

   protected void back() {
      view.back();
   }

   private void updateEntityComments(Comment comment) {
      int commentIndex = feedEntity.getComments().indexOf(comment);
      if (commentIndex != -1) {
         feedEntity.getComments().set(commentIndex, comment);
      }
   }

   public interface View extends RxView, FlagDelegate.View {

      void addComments(List<Comment> commentList);

      void addComment(Comment comment);

      void removeComment(Comment comment);

      void updateComment(Comment comment);

      void enablePostButton(boolean enable);

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

package com.worldventures.dreamtrips.modules.feed.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.DeleteItemHttpAction;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.UidItemDelegate;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.api.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.api.GetUsersLikedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.event.DeleteBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadMoreEvent;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.GetCommentsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class BaseCommentPresenter<T extends BaseCommentPresenter.View> extends Presenter<T> {

   @Inject FeedEntityManager entityManager;
   @Inject BucketInteractor bucketInteractor;
   @Inject TranslationFeedInteractor translationFeedInteractor;
   @Inject CommentsInteractor commentsInteractor;
   @Inject LocaleHelper localeHelper;
   @Inject FlagsInteractor flagsInteractor;

   private UidItemDelegate uidItemDelegate;

   @State FeedEntity feedEntity;
   @State String draftComment;

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
      uidItemDelegate = new UidItemDelegate(this, flagsInteractor);
   }

   @Override
   public void takeView(T view) {
      super.takeView(view);
      apiErrorPresenter.setView(view);
      view.setDraftComment(draftComment);
      view.setLikePanel(feedEntity);

      if (isNeedCheckCommentsWhenStart()) checkCommentsAndLikesToLoad();

      subscribeToCommentsLoading();
      subscribeToCommentTranslation();
   }

   private void subscribeToCommentTranslation() {
      view.bindUntilDropView(translationFeedInteractor.translateCommentPipe()
            .observe()
            .compose(new IoToMainComposer<>()))
            .subscribe(new ActionStateSubscriber<TranslateUidItemCommand.TranslateCommentCommand>().onSuccess(translateCommentCommand -> {
               updateEntityComments(translateCommentCommand.getResult());
               view.updateComment(translateCommentCommand.getResult());
            }).onFail((translateCommentCommand, throwable) -> {
               view.notifyDataSetChanged();
               view.informUser(translateCommentCommand.getErrorMessage());
            }));
   }

   protected void checkCommentsAndLikesToLoad() {
      if (loadInitiated) return;
      //
      loadComments();
      loadInitiated = true;
      loadLikes();
   }

   protected boolean isNeedCheckCommentsWhenStart() {
      return true;
   }

   private void loadComments() {
      view.setLoading(true);
      commentsInteractor.commentsPipe().send(new GetCommentsCommand(feedEntity.getUid(), page));
   }

   private void subscribeToCommentsLoading() {
      view.bindUntilDropView(commentsInteractor.commentsPipe().observe().compose(new IoToMainComposer<>()))
            .subscribe(new ActionStateSubscriber<GetCommentsCommand>()
                  .onSuccess(getCommentsCommand -> onCommentsLoaded(getCommentsCommand.getResult()))
                  .onFail((getCommentsCommand, throwable) -> view.informUser(getCommentsCommand.getErrorMessage())));
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

   private void loadLikes() {
      doRequest(new GetUsersLikedEntityQuery(feedEntity.getUid(), 1, 2), this::onLikersLoaded);
   }

   public void setDraftComment(String comment) {
      this.draftComment = comment;
   }

   public void post() {
      entityManager.createComment(feedEntity, draftComment);
   }

   public void loadFlags(Flaggable flaggableView) {
      uidItemDelegate.loadFlags(flaggableView, this::handleError);
   }

   public void flagItem(String uid, int reasonId, String reason) {
      uidItemDelegate.flagItem(new FlagData(uid, reasonId, reason), view);
   }

   public void editComment(Comment comment) {
      view.editComment(feedEntity, comment);
      sendAnalytic(TrackingHelper.ATTRIBUTE_EDIT_COMMENT);
   }

   public void deleteComment(Comment comment) {
      entityManager.deleteComment(feedEntity, comment);
   }

   public void translateComment(Comment comment) {
      translationFeedInteractor.translateCommentPipe()
            .send(TranslateUidItemCommand.forComment(comment, localeHelper.getDefaultLocaleFormatted()));
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

   public void onEvent(EditBucketEvent event) {
      if (!view.isVisibleOnScreen()) return;
      //
      BucketBundle bundle = new BucketBundle();
      bundle.setType(event.type());
      bundle.setBucketItem(event.bucketItem());

      view.showEdit(bundle);
   }

   public void onEvent(DeletePostEvent event) {
      if (view.isVisibleOnScreen()) doRequest(new DeletePostCommand(event.getEntity()
            .getUid()), aVoid -> itemDeleted(event.getEntity()));
   }

   public void onEvent(DeletePhotoEvent event) {
      if (view.isVisibleOnScreen()) doRequest(new DeletePhotoCommand(event.getEntity()
            .getUid()), aVoid -> itemDeleted(event.getEntity()));
   }

   public void onEvent(DeleteBucketEvent event) {
      if (view.isVisibleOnScreen()) {
         BucketItem bucketItemToDelete = event.getEntity();

         view.bind(bucketInteractor.deleteItemPipe()
               .createObservable(new DeleteItemHttpAction(bucketItemToDelete.getUid()))
               .observeOn(AndroidSchedulers.mainThread()))
               .subscribe(new ActionStateSubscriber<DeleteItemHttpAction>().onSuccess(deleteItemAction -> itemDeleted(bucketItemToDelete))
                     .onFail((deleteItemAction, throwable) -> {
                        view.setLoading(false); //TODO: review, after leave from robospice completely
                        handleError(deleteItemAction, throwable);
                     }));
      }
   }

   public void onEvent(LoadFlagEvent event) {
      if (view.isVisibleOnScreen()) uidItemDelegate.loadFlags(event.getFlaggableView(), this::handleError);
   }

   public void onEvent(ItemFlaggedEvent event) {
      if (view.isVisibleOnScreen()) uidItemDelegate.flagItem(new FlagData(event.getEntity()
            .getUid(), event.getFlagReasonId(), event.getNameOfReason()), view);
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

   private void updateEntityComments(Comment comment) {
      int commentIndex = feedEntity.getComments().indexOf(comment);
      if (commentIndex != -1) feedEntity.getComments().set(commentIndex, comment);
   }

   public interface View extends RxView, UidItemDelegate.View, ApiErrorView {

      void addComments(List<Comment> commentList);

      void addComment(Comment comment);

      void removeComment(Comment comment);

      void updateComment(Comment comment);

      void setDraftComment(String comment);

      void setLoading(boolean loading);

      void notifyDataSetChanged();

      void editComment(FeedEntity feedEntity, Comment comment);

      void hideViewMore();

      void onPostError();

      void showViewMore();

      void showEdit(BucketBundle bucketBundle);

      void setLikePanel(FeedEntity entity);

      void back();
   }
}

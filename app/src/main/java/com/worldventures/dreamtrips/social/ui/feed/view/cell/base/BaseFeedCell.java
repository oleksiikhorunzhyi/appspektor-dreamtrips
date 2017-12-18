package com.worldventures.dreamtrips.social.ui.feed.view.cell.base;

import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.ViewFeedEntityAction;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.social.ui.feed.view.util.ActionPanelViewShareHandler;
import com.worldventures.dreamtrips.social.ui.feed.view.util.LikersPanelHelper;
import com.worldventures.dreamtrips.social.ui.friends.bundle.UsersLikedEntityBundle;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.UsersLikedItemFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import javax.inject.Inject;

import butterknife.InjectView;

public abstract class BaseFeedCell<ITEM extends FeedItem, DELEGATE extends BaseFeedCell.FeedCellDelegate<ITEM>> extends BaseAbstractDelegateCell<ITEM, DELEGATE> {

   @Inject Presenter.TabletAnalytic tabletAnalytic;
   @Inject protected SessionHolder sessionHolder;
   @Inject protected FragmentManager fragmentManager;
   @Inject protected Router router;
   @Inject protected AnalyticsInteractor analyticsInteractor;

   @InjectView(R.id.actionView) FeedActionPanelView actionView;
   @InjectView(R.id.likers_panel) TextView likersPanel;

   private LikersPanelHelper likersPanelHelper;
   private NavigationWrapper navigationWrapper;
   private ActionPanelViewShareHandler feedActionHandler;
   private boolean syncUIStateWithModelWasCalled;

   public BaseFeedCell(View view) {
      super(view);
      likersPanelHelper = new LikersPanelHelper();
   }

   @Override
   public void afterInject() {
      super.afterInject();
      navigationWrapper = new NavigationWrapperFactory().componentOrDialogNavigationWrapper(router, fragmentManager, tabletAnalytic);
   }

   @Override
   protected void syncUIStateWithModel() {
      syncUIStateWithModelWasCalled = true;
      //
      actionView.setState(getModelObject(), isMineItem(getModelObject()));
      actionView.setOnLikeIconClickListener(feedItem -> cellDelegate.onLikeItem(getModelObject()));
      actionView.setOnLikersClickListener(feedItem ->
            navigationWrapper.navigate(UsersLikedItemFragment.class, new UsersLikedEntityBundle(feedItem.getItem(),
                  feedItem.getItem().getLikesCount())));
      actionView.setOnCommentIconClickListener(feedItem -> cellDelegate.onCommentItem(getModelObject()));
      actionView.setOnMoreClickListener(feedItem -> onMore());
      actionView.setOnDeleteClickListener(feedItem -> onDelete());
      actionView.setOnEditClickListener(feedItem -> onEdit());
      actionView.setOnFlagClickListener(feedItem -> cellDelegate.onLoadFlags(actionView));
      actionView.setOnFlagDialogClickListener((feedItem, flagReasonId, reason)
            -> cellDelegate.onFlagChosen(feedItem, flagReasonId, reason));
      feedActionHandler = new ActionPanelViewShareHandler(router, analyticsInteractor);
      feedActionHandler.init(actionView, cellDelegate::onDownloadImage);
      //
      if (likersPanel != null) {
         likersPanelHelper.setup(likersPanel, getModelObject().getItem());
         likersPanel.setOnClickListener(v -> navigationWrapper.navigate(UsersLikedItemFragment.class,
               new UsersLikedEntityBundle(getModelObject().getItem(), getModelObject().getItem().getLikesCount())));
      }

      cellDelegate.onEntityShownInCell(getModelObject());
   }

   private boolean isMineItem(FeedItem feedItem) {
      Optional<UserSession> userSessionOptional = sessionHolder.get();
      if (feedItem.getItem().getOwner() == null || !userSessionOptional.isPresent()) {
         return false;
      }

      int accountId = userSessionOptional.get().user().getId();
      int ownerId = feedItem.getItem().getOwner().getId();
      return accountId == ownerId;
   }

   @Override
   public void fillWithItem(ITEM item) {
      syncUIStateWithModelWasCalled = false;
      super.fillWithItem(item);
      if (!syncUIStateWithModelWasCalled) {
         throw new IllegalStateException("super.syncUIStateWithModel was not called");
      }
   }

   public void setLikersPanelListener(LikersPanelHelper.LikersPanelListener likersPanelListener) {
      likersPanelHelper.setLikersPanelListener(likersPanelListener);
   }

   protected void showMoreDialog(@MenuRes int menuRes, @StringRes int headerDelete, @StringRes int textDelete) {
      actionView.showMoreDialog(menuRes, headerDelete, textDelete);
   }

   protected void onDelete() {
      analyticsInteractor.analyticsActionPipe().send(ViewFeedEntityAction.delete(getModelObject().getType(),
            getModelObject().getItem().getUid()));
   }

   protected void onEdit() {
      analyticsInteractor.analyticsActionPipe().send(ViewFeedEntityAction.edit(getModelObject().getType(),
            getModelObject().getItem().getUid()));
   }

   protected void onMore() {
      //do nothing
   }

   public interface FeedCellDelegate<ITEM> extends CellDelegate<ITEM> {

      void onEntityShownInCell(ITEM item);

      void onLikeItem(ITEM item);

      void onCommentItem(ITEM item);

      void onDownloadImage(String url);

      void onLoadFlags(Flaggable flaggableView);

      void onFlagChosen(FeedItem feedItem, int flagReasonId, String reason);

      void onEditTextualPost(TextualPost textualPost);

      void onDeleteTextualPost(TextualPost textualPost);

      void onDeleteVideo(Video video);

      void onTranslateItem(FeedEntity translatableItem);

      void onShowOriginal(FeedEntity translatableItem);

      void onEditPhoto(Photo photo);

      void onDeletePhoto(Photo photo);

      void onEditBucketItem(BucketItem bucketItem, BucketItem.BucketType type);

      void onDeleteBucketItem(BucketItem bucketItem);
   }
}

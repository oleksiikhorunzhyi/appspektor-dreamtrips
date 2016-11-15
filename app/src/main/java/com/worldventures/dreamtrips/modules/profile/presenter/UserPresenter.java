package com.worldventures.dreamtrips.modules.profile.presenter;

import android.support.annotation.StringRes;

import com.innahema.collections.query.functions.Action1;
import com.messenger.delegate.FlagsInteractor;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.ui.activity.MessengerActivity;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.modules.common.api.janet.command.CirclesCommand;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.UidItemDelegate;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.service.NotificationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.GetUserTimelineCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationAsReadCommand;
import com.worldventures.dreamtrips.modules.friends.janet.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.modules.friends.janet.AddFriendCommand;
import com.worldventures.dreamtrips.modules.friends.janet.FriendsInteractor;
import com.worldventures.dreamtrips.modules.friends.janet.RemoveFriendCommand;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.profile.api.GetPublicProfileQuery;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.event.FriendGroupRelationChangedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserPresenter extends ProfilePresenter<UserPresenter.View, User> {

   @Inject CirclesInteractor circlesInteractor;
   @Inject FriendsInteractor friendsInteractor;
   @Inject NotificationFeedInteractor notificationFeedInteractor;
   @Inject NotificationDelegate notificationDelegate;
   @Inject StartChatDelegate startSingleChatDelegate;
   @Inject FlagsInteractor flagsInteractor;

   private int notificationId;
   private boolean acceptFriend;
   private UidItemDelegate uidItemDelegate;

   public UserPresenter(UserBundle userBundle) {
      super(userBundle.getUser());
      this.notificationId = userBundle.getNotificationId();
      this.acceptFriend = userBundle.isAcceptFriend();
      userBundle.resetNotificationId();
      userBundle.resetAcceptFriend();
   }

   @Override
   public void onInjected() {
      super.onInjected();
      notificationDelegate.cancel(user.getId());
      uidItemDelegate = new UidItemDelegate(this, flagsInteractor);
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      apiErrorPresenter.setView(view);
      subscribeLoadNextFeeds();
      subscribeRefreshFeeds();
   }

   @Override
   public void onStart() {
      super.onStart();
      if (notificationId != UserBundle.NO_NOTIFICATION)
         notificationFeedInteractor.markNotificationPipe()
               .send(new MarkNotificationAsReadCommand(notificationId));
      if (acceptFriend) {
         acceptClicked();
         acceptFriend = false;
      }
   }

   private void subscribeRefreshFeeds() {
      view.bindUntilDropView(feedInteractor.getRefreshUserTimelinePipe().observe().compose(new IoToMainComposer<>()))
            .subscribe(new ActionStateSubscriber<GetUserTimelineCommand.Refresh>().onFail(this::refreshFeedError)
                  .onSuccess(action -> refreshFeedSucceed(action.getResult())));
   }


   private void subscribeLoadNextFeeds() {
      view.bindUntilDropView(feedInteractor.getLoadNextUserTimelinePipe().observe().compose(new IoToMainComposer<>()))
            .subscribe(new ActionStateSubscriber<GetUserTimelineCommand.LoadNext>().onFail(this::loadMoreItemsError)
                  .onSuccess(action -> addFeedItems(action.getResult())));
   }

   @Override
   public void refreshFeed() {
      feedInteractor.getRefreshUserTimelinePipe().send(new GetUserTimelineCommand.Refresh(user.getId()));
   }

   @Override
   public void loadNext(Date date) {
      feedInteractor.getLoadNextUserTimelinePipe().send(new GetUserTimelineCommand.LoadNext(user.getId(), date));
   }

   @Override
   protected void loadProfile() {
      view.startLoading();
      doRequest(new GetPublicProfileQuery(user), this::onProfileLoaded, spiceException -> {
         view.finishLoading();
         super.handleError(spiceException);
      });
   }

   public void onStartChatClicked() {
      startSingleChatDelegate.startSingleChat(user, conversation -> MessengerActivity.startMessengerWithConversation(activityRouter
            .getContext(), conversation.getId()));
   }

   public void addFriendClicked() {
      User.Relationship userRelationship = user.getRelationship();
      if (userRelationship == null) return;

      switch (userRelationship) {
         case REJECTED:
         case NONE:
            addFriend();
            break;
         case FRIEND:
            view.showFriendDialog(user);
            break;
      }
   }

   public void openPrefs(User user) {
      view.openFriendPrefs(new UserBundle(user));
   }

   public void unfriend() {
      friendsInteractor.removeFriendPipe()
            .createObservable(new RemoveFriendCommand(user))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<RemoveFriendCommand>()
                  .onStart(action -> view.startLoading())
                  .onSuccess(action -> {
                     view.finishLoading();
                     user.unfriend();
                     view.notifyUserChanged();
                  })
                  .onFail((action, e) -> onError(action)));
   }

   private void addFriend() {
      showAddFriendDialog(this::addAsFriend);
   }

   private void addAsFriend(Circle circle) {
      friendsInteractor.addFriendPipe()
            .createObservable(new AddFriendCommand(user, circle.getId()))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<AddFriendCommand>()
                  .onStart(action -> view.startLoading())
                  .onSuccess(action -> {
                     user.setRelationship(User.Relationship.OUTGOING_REQUEST);
                     view.finishLoading();
                     view.notifyUserChanged();
                  })
                  .onFail((action, e) -> onError(action)));
   }

   public void acceptClicked() {
      showAddFriendDialog(this::accept);
   }

   private void accept(Circle circle) {
      friendsInteractor.acceptRequestPipe()
            .createObservable(new ActOnFriendRequestCommand.Accept(user, circle.getId()))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<ActOnFriendRequestCommand.Accept>()
                  .onStart(action -> view.startLoading())
                  .onSuccess(action -> {
                     view.finishLoading();
                     user.setRelationship(User.Relationship.FRIEND);
                     view.notifyUserChanged();
                  })
                  .onFail((action, e) -> onError(action)));
   }

   public void rejectClicked() {
      reject();
   }

   private void reject() {
      friendsInteractor.rejectRequestPipe()
            .createObservable(new ActOnFriendRequestCommand.Reject(user))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<ActOnFriendRequestCommand.Reject>()
                  .onStart(action -> view.startLoading())
                  .onSuccess(action -> {
                     view.finishLoading();
                     user.setRelationship(User.Relationship.REJECTED);
                     view.notifyUserChanged();
                  })
                  .onFail((action, e) -> onError(action)));
   }

   private void onError(CommandWithError commandWithError) {
      view.finishLoading();
      view.informUser(commandWithError.getErrorMessage());
   }

   public void onEvent(FriendGroupRelationChangedEvent event) {
      if (user.getId() == event.getFriend().getId()) {
         switch (event.getState()) {
            case REMOVED:
               user.getCircles().remove(event.getCircle());
               break;
            case ADDED:
               user.getCircles().add(event.getCircle());
               break;
         }
         view.notifyUserChanged();
      }
   }

   @Override
   public void openBucketList() {
      view.openBucketList(Route.FOREIGN_BUCKET_TABS, new ForeignBucketTabsBundle(user));
   }

   @Override
   public void openTripImages() {
      view.openTripImages(Route.TRIP_LIST_IMAGES, new TripsImagesBundle(TripImagesType.ACCOUNT_IMAGES, user.getId()));
   }

   public void onEvent(LoadFlagEvent event) {
      if (view.isVisibleOnScreen()) uidItemDelegate.loadFlags(event.getFlaggableView(), this::handleError);
   }

   public void onEvent(ItemFlaggedEvent event) {
      if (view.isVisibleOnScreen()) uidItemDelegate.flagItem(new FlagData(event.getEntity()
            .getUid(), event.getFlagReasonId(), event.getNameOfReason()), view);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Circles
   ///////////////////////////////////////////////////////////////////////////

   private void showAddFriendDialog(Action1<Circle> actionCircle) {
      circlesInteractor.pipe()
            .createObservable(new CirclesCommand())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<CirclesCommand>().onStart(circlesCommand -> onCirclesStart())
                  .onSuccess(circlesCommand -> onCirclesSuccess(circlesCommand.getResult(), actionCircle))
                  .onFail((circlesCommand, throwable) -> onCirclesError(circlesCommand.getErrorMessage())));
   }

   private void onCirclesStart() {
      view.showBlockingProgress();
   }

   private void onCirclesSuccess(List<Circle> resultCircles, Action1<Circle> actionCircle) {
      view.showAddFriendDialog(resultCircles, actionCircle);
      view.hideBlockingProgress();
   }

   private void onCirclesError(@StringRes String messageId) {
      view.hideBlockingProgress();
      view.informUser(messageId);
   }

   public interface View extends ProfilePresenter.View, UidItemDelegate.View, BlockingProgressView, ApiErrorView {

      void showAddFriendDialog(List<Circle> circles, Action1<Circle> selectAction);

      void showFriendDialog(User user);

      void openFriendPrefs(UserBundle userBundle);
   }
}

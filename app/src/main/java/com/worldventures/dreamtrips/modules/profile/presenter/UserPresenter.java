package com.worldventures.dreamtrips.modules.profile.presenter;

import com.innahema.collections.query.functions.Action1;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.ui.activity.MessengerActivity;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.modules.common.api.janet.command.GetCirclesCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.modules.feed.service.NotificationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.GetUserTimelineCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationAsReadCommand;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.service.FriendsInteractor;
import com.worldventures.dreamtrips.modules.friends.service.command.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.AddFriendCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.RemoveFriendCommand;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.service.ProfileInteractor;
import com.worldventures.dreamtrips.modules.profile.service.command.GetPublicProfileCommand;
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
   @Inject ProfileInteractor profileInteractor;

   private int notificationId;
   private boolean acceptFriend;

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
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      subscribeLoadNextFeeds();
      subscribeRefreshFeeds();
      subscribeToChangingCircles();
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
      profileInteractor.publicProfilePipe().createObservable(new GetPublicProfileCommand(user.getId()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetPublicProfileCommand>()
            .onSuccess(command -> this.onProfileLoaded(command.getResult()))
            .onFail((getPublicProfileCommand, throwable) -> {
               view.finishLoading();
               handleError(getPublicProfileCommand, throwable);
            }));
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
                  .onFail(this::onError));
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
                  .onFail(this::onError));
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
                  .onFail(this::onError));
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
                  .onFail(this::onError));
   }

   private void onError(CommandWithError commandWithError, Throwable throwable) {
      view.finishLoading();
      handleError(commandWithError, throwable);
   }

   private void subscribeToChangingCircles() {
      profileInteractor.addFriendToCirclesPipe().observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> {
               if (user.getId() == command.getUserId()) {
                  user.getCircles().add(command.getCircle());
                  view.notifyUserChanged();
               }
            });
      profileInteractor.removeFriendFromCirclesPipe().observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> {
               if (user.getId() == command.getUserId()) {
                  user.getCircles().remove(command.getCircle());
                  view.notifyUserChanged();
               }
            });
   }

   @Override
   public void openBucketList() {
      view.openBucketList(Route.FOREIGN_BUCKET_TABS, new ForeignBucketTabsBundle(user));
   }

   @Override
   public void openTripImages() {
      view.openTripImages(Route.TRIP_LIST_IMAGES, new TripsImagesBundle(TripImagesType.ACCOUNT_IMAGES_FROM_PROFILE, user.getId()));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Circles
   ///////////////////////////////////////////////////////////////////////////

   private void showAddFriendDialog(Action1<Circle> actionCircle) {
      circlesInteractor.pipe()
            .createObservable(new GetCirclesCommand())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<GetCirclesCommand>().onStart(circlesCommand -> onCirclesStart())
                  .onSuccess(circlesCommand -> onCirclesSuccess(circlesCommand.getResult(), actionCircle))
                  .onFail((getCirclesCommand, throwable) -> {
                     view.hideBlockingProgress();
                     handleError(getCirclesCommand, throwable);
                  }));
   }

   private void onCirclesStart() {
      view.showBlockingProgress();
   }

   private void onCirclesSuccess(List<Circle> resultCircles, Action1<Circle> actionCircle) {
      view.showAddFriendDialog(resultCircles, actionCircle);
      view.hideBlockingProgress();
   }

   public interface View extends ProfilePresenter.View, BlockingProgressView, ApiErrorView {

      void showAddFriendDialog(List<Circle> circles, Action1<Circle> selectAction);

      void showFriendDialog(User user);

      void openFriendPrefs(UserBundle userBundle);
   }
}

package com.worldventures.dreamtrips.social.ui.profile.presenter;

import com.innahema.collections.query.functions.Action1;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.ui.activity.MessengerActivity;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.model.Circle;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.social.service.profile.ProfileInteractor;
import com.worldventures.dreamtrips.social.service.profile.analytics.FriendRelationshipAnalyticAction;
import com.worldventures.dreamtrips.social.service.profile.command.GetPublicProfileCommand;
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor;
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor;
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesCommand;
import com.worldventures.dreamtrips.social.service.users.friend.command.RemoveFriendCommand;
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.social.service.users.search.command.AddFriendCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.ForeignBucketTabsFragment;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.social.ui.feed.service.NotificationFeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetUserTimelineCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.MarkNotificationAsReadCommand;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.UserTimelineStorageDelegate;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserPresenter extends ProfilePresenter<UserPresenter.View> {

   @Inject CirclesInteractor circlesInteractor;
   @Inject FriendsInteractor friendsInteractor;
   @Inject NotificationFeedInteractor notificationFeedInteractor;
   @Inject NotificationDelegate notificationDelegate;
   @Inject StartChatDelegate startSingleChatDelegate;
   @Inject ProfileInteractor profileInteractor;
   @Inject UserTimelineStorageDelegate userTimelineStorageDelegate;

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
   public void onViewTaken() {
      super.onViewTaken();
      subscribeToStorage();
      subscribeLoadNextFeeds();
      subscribeRefreshFeeds();
      subscribeToChangingCircles();
   }

   @Override
   public void onStart() {
      super.onStart();
      if (notificationId != UserBundle.NO_NOTIFICATION) {
         notificationFeedInteractor.markNotificationPipe()
               .send(new MarkNotificationAsReadCommand(notificationId));
      }
      if (acceptFriend) {
         acceptClicked();
         acceptFriend = false;
      }
   }

   void subscribeToStorage() {
      userTimelineStorageDelegate.setUserId(user.getId());
      userTimelineStorageDelegate.observeStorageCommand()
            .compose(bindViewToMainComposer())
            .map(Command::getResult)
            .subscribe(this::onItemsChanged, this::handleError);
   }

   void subscribeRefreshFeeds() {
      feedInteractor.getRefreshUserTimelinePipe()
            .observe().compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetUserTimelineCommand.Refresh>()
                  .onSuccess(action -> refreshFeedSucceed(action.getResult()))
                  .onFail(this::refreshFeedError));

   }

   void subscribeLoadNextFeeds() {
      feedInteractor.getLoadNextUserTimelinePipe()
            .observe().compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetUserTimelineCommand.LoadNext>()
                  .onSuccess(action -> addFeedItems(action.getResult()))
                  .onFail(this::loadMoreItemsError));
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
      profileInteractor.getPublicProfilePipe().createObservable(new GetPublicProfileCommand(user.getId()))
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
      if (userRelationship == null) {
         return;
      }

      switch (userRelationship) {
         case REJECTED:
         case NONE:
            addFriend();
            break;
         case FRIEND:
            view.showFriendDialog(user);
            break;
         default:
            break;
      }
   }

   public void openPrefs(User user) {
      view.openFriendPrefs(new UserBundle(user));
   }

   public void unfriend() {
      analyticsInteractor.analyticsActionPipe().send(FriendRelationshipAnalyticAction.unfriend());
      friendsInteractor.getRemoveFriendPipe()
            .createObservable(new RemoveFriendCommand(user))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<RemoveFriendCommand>()
                  .onStart(action -> view.startLoading())
                  .onSuccess(action -> {
                     view.finishLoading();
                     user.unfriend();
                     refreshFeedItems();
                  })
                  .onFail(this::onError));
   }

   private void addFriend() {
      showAddFriendDialog(this::addAsFriend);
   }

   private void addAsFriend(Circle circle) {
      friendsInteractor.getAddFriendPipe()
            .createObservable(new AddFriendCommand(user, circle.getId()))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<AddFriendCommand>()
                  .onStart(action -> view.startLoading())
                  .onSuccess(action -> {
                     user.setRelationship(User.Relationship.OUTGOING_REQUEST);
                     view.finishLoading();
                     refreshFeedItems();
                     view.notifyDataSetChanged();
                  })
                  .onFail(this::onError));
   }

   public void acceptClicked() {
      showAddFriendDialog(this::accept);
   }

   private void accept(Circle circle) {
      friendsInteractor.getAcceptRequestPipe()
            .createObservable(new ActOnFriendRequestCommand.Accept(user, circle.getId()))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<ActOnFriendRequestCommand.Accept>()
                  .onStart(action -> view.startLoading())
                  .onSuccess(action -> {
                     view.finishLoading();
                     user.setRelationship(User.Relationship.FRIEND);
                     refreshFeedItems();
                     view.notifyDataSetChanged();
                  })
                  .onFail(this::onError));
   }

   public void rejectClicked() {
      reject();
   }

   private void reject() {
      friendsInteractor.getRejectRequestPipe()
            .createObservable(new ActOnFriendRequestCommand.Reject(user))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<ActOnFriendRequestCommand.Reject>()
                  .onStart(action -> view.startLoading())
                  .onSuccess(action -> {
                     view.finishLoading();
                     user.setRelationship(User.Relationship.REJECTED);
                     refreshFeedItems();
                     view.notifyDataSetChanged();
                  })
                  .onFail(this::onError));
   }

   private void onError(CommandWithError commandWithError, Throwable throwable) {
      view.finishLoading();
      handleError(commandWithError, throwable);
   }

   void subscribeToChangingCircles() {
      profileInteractor.getAddFriendToCirclePipe().observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> {
               if (user.getId() == command.getUserId()) {
                  user.getCircles().add(command.getCircle());
                  refreshFeedItems();
                  view.notifyDataSetChanged();
               }
            });
      profileInteractor.getRemoveFriendFromCirclePipe().observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> {
               if (user.getId() == command.getUserId()) {
                  user.getCircles().remove(command.getCircle());
                  refreshFeedItems();
                  view.notifyDataSetChanged();
               }
            });
   }

   @Override
   public void openBucketList() {
      view.openBucketList(ForeignBucketTabsFragment.class, new ForeignBucketTabsBundle(user));
   }

   @Override
   public void openTripImages() {
      view.openTripImages(TripImagesArgs.builder()
            .userId(user.getId())
            .origin(CreateEntityBundle.Origin.PROFILE_TRIP_IMAGES)
            .type(TripImagesArgs.TripImageType.ACCOUNT_IMAGES)
            .build());
   }

   ///////////////////////////////////////////////////////////////////////////
   // Circles
   ///////////////////////////////////////////////////////////////////////////

   private void showAddFriendDialog(Action1<Circle> actionCircle) {
      circlesInteractor.getPipe()
            .createObservable(new GetCirclesCommand())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<GetCirclesCommand>().onStart(circlesCommand -> onCirclesStart())
                  .onSuccess(circlesCommand -> onCirclesSuccess(new ArrayList<>(circlesCommand.getResult()), actionCircle))
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

   public interface View extends ProfilePresenter.View, BlockingProgressView {

      void showAddFriendDialog(List<Circle> circles, Action1<Circle> selectAction);

      void showFriendDialog(User user);

      void openFriendPrefs(UserBundle userBundle);
   }
}

package com.worldventures.dreamtrips.modules.friends.presenter;

import com.innahema.collections.query.functions.Action1;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.ui.activity.MessengerActivity;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.modules.common.api.janet.command.GetCirclesCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.service.FriendsInteractor;
import com.worldventures.dreamtrips.modules.friends.service.command.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.AddFriendCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.GetUsersCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.RemoveFriendCommand;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.service.ProfileInteractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public abstract class BaseUserListPresenter<T extends BaseUserListPresenter.View> extends Presenter<T> {

   protected List<User> users = new ArrayList<>();
   private int previousTotal = 0;
   private boolean loading = true;
   private int nextPage = 1;
   private boolean loadUsersRequestLocked;

   @Inject StartChatDelegate startChatDelegate;
   @Inject CirclesInteractor circlesInteractor;
   @Inject FriendsInteractor friendsInteractor;
   @Inject ProfileInteractor profileInteractor;

   @Override
   public void takeView(T view) {
      super.takeView(view);
      subscribeToChangingCircles();
      subscribeToRemovedFriends();
      if (isNeedPreload()) reload();
   }

   private void subscribeToRemovedFriends() {
      friendsInteractor.removeFriendPipe()
            .observeSuccess()
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(action -> {
               userStateChanged(action.getResult());
            });
   }

   protected Observable<ActionState<GetCirclesCommand>> getCirclesObservable() {
      return circlesInteractor.pipe()
            .createObservable(new GetCirclesCommand())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView());
   }

   protected void onCirclesStart() {
      view.showBlockingProgress();
   }

   protected void onCirclesSuccess(List<Circle> resultCircles) {
      Collections.sort(resultCircles);
      view.hideBlockingProgress();
   }

   protected void onCirclesError(CommandWithError commandWithError, Throwable throwable) {
      view.hideBlockingProgress();
      handleError(commandWithError, throwable);
   }

   protected boolean isNeedPreload() {
      return true;
   }

   public void reload() {
      loadUsersRequestLocked = true;
      nextPage = 1;
      resetLazyLoadFields();
      view.startLoading();
      loadUsers(nextPage, this::onUsersLoaded);
   }

   protected void onUsersLoaded(List<User> freshUsers) {
      loadUsersRequestLocked = false;
      nextPage++;
      users.clear();
      users.addAll(freshUsers);
      view.finishLoading();
      view.refreshUsers(users);
   }

   protected void onUsersAdded(List<User> freshUsers) {
      loadUsersRequestLocked = false;
      nextPage++;
      users.addAll(freshUsers);
      view.refreshUsers(users);
      view.finishLoading();
   }

   protected void onError(GetUsersCommand getUsersCommand, Throwable throwable) {
      view.finishLoading();
      handleError(getUsersCommand, throwable);
      loadUsersRequestLocked = false;
      loading = false;
   }

   protected abstract void loadUsers(int page, rx.functions.Action1<List<User>> onSuccessAction);

   public void scrolled(int totalItemCount, int lastVisible) {
      if (totalItemCount > previousTotal) {
         loading = false;
         previousTotal = totalItemCount;
      }
      if (!loading && !loadUsersRequestLocked && lastVisible >= totalItemCount - 1) {
         view.startLoading();
         loading = true;
         loadUsersRequestLocked = true;

         loadUsers(nextPage, this::onUsersAdded);
      }
   }

   private void resetLazyLoadFields() {
      previousTotal = 0;
      loading = false;
   }

   protected void acceptRequest(User user) {
      getCirclesObservable().subscribe(new ActionStateSubscriber<GetCirclesCommand>().onStart(circlesCommand -> onCirclesStart())
            .onSuccess(circlesCommand -> onCirclesSuccessAcceptRequest(user, circlesCommand.getResult()))
            .onFail(this::onCirclesError));
   }

   private void onCirclesSuccessAcceptRequest(User user, List<Circle> circles) {
      onCirclesSuccess(circles);
      view.showAddFriendDialog(circles, position ->
            friendsInteractor.acceptRequestPipe()
                  .createObservable(new ActOnFriendRequestCommand.Accept(user, circles.get(position).getId()))
                  .compose(bindView())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new ActionStateSubscriber<ActOnFriendRequestCommand.Accept>()
                        .onStart(action -> view.startLoading())
                        .onSuccess(action -> {
                           user.setRelationship(User.Relationship.FRIEND);
                           userActionSucceed(user);
                        })
                        .onFail(this::onError))
      );
   }

   protected void addFriend(User user) {
      getCirclesObservable().subscribe(new ActionStateSubscriber<GetCirclesCommand>().onStart(circlesCommand -> onCirclesStart())
            .onSuccess(circlesCommand -> onCirclesSuccessAddUserRequest(user, circlesCommand.getResult()))
            .onFail(this::onCirclesError));
   }

   private void onCirclesSuccessAddUserRequest(User user, List<Circle> circles) {
      onCirclesSuccess(circles);
      view.showAddFriendDialog(circles, arg -> {
         addFriend(user, circles.get(arg));
      });
   }

   public void openPrefs(User user) {
      view.openFriendPrefs(new UserBundle(user));
   }

   public void startChat(User user) {
      startChatDelegate.startSingleChat(user, conversation -> MessengerActivity.startMessengerWithConversation(activityRouter
            .getContext(), conversation.getId()));
   }

   private void subscribeToChangingCircles() {
      profileInteractor.addFriendToCirclesPipe().observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> {
               for (User friend : users) {
                  if (friend.getId() == command.getUserId()) {
                     friend.getCircles().add(command.getCircle());
                     break;
                  }
               }
               view.refreshUsers(users);
            });
      profileInteractor.removeFriendFromCirclesPipe().observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> {
               for (User friend : users) {
                  if (friend.getId() == command.getUserId()) {
                     friend.getCircles().remove(command.getCircle());
                     break;
                  }
               }
               view.refreshUsers(users);
            });
   }

   private void userActionSucceed(User user) {
      if (view != null) {
         view.finishLoading();
         userStateChanged(user);
      }
   }

   protected abstract void userStateChanged(User user);

   public void unfriend(User user) {
      friendsInteractor.removeFriendPipe()
            .createObservable(new RemoveFriendCommand(user))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<RemoveFriendCommand>()
                  .onStart(action -> view.startLoading())
                  .onSuccess(action -> {
                     view.finishLoading();
                     user.setRelationship(User.Relationship.NONE);
                     userActionSucceed(user);
                  })
                  .onFail(this::onError));
   }

   private void addFriend(User user, Circle circle) {
      friendsInteractor.addFriendPipe()
            .createObservable(new AddFriendCommand(user, circle.getId()))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<AddFriendCommand>()
                  .onStart(action -> view.startLoading())
                  .onSuccess(action -> {
                     view.finishLoading();
                     user.setRelationship(User.Relationship.OUTGOING_REQUEST);
                     userStateChanged(user);
                  })
                  .onFail(this::onError));
   }

   protected int getPerPageCount() {
      return 100;
   }

   private void onError(CommandWithError commandWithError, Throwable throwable) {
      view.finishLoading();
      handleError(commandWithError, throwable);
   }

   public void userClicked(User user) {
      view.openUser(new UserBundle(user));
   }

   public interface View extends Presenter.View, BlockingProgressView {

      void startLoading();

      void finishLoading();

      void refreshUsers(List<User> users);

      void openFriendPrefs(UserBundle userBundle);

      void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectAction);

      void openUser(UserBundle userBundle);
   }
}

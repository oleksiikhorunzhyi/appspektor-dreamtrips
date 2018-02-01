package com.worldventures.dreamtrips.social.ui.friends.presenter;

import android.support.annotation.VisibleForTesting;

import com.innahema.collections.query.functions.Action1;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.ui.activity.MessengerActivity;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.model.Circle;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.social.service.friends.interactor.CirclesInteractor;
import com.worldventures.dreamtrips.social.service.friends.interactor.FriendsInteractor;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.AddFriendCommand;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetCirclesCommand;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetUsersCommand;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.RemoveFriendCommand;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor;
import com.worldventures.dreamtrips.social.ui.profile.service.analytics.FriendRelationshipAnalyticAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

@SuppressWarnings("WeakerAccess")
public abstract class BaseUserListPresenter<T extends BaseUserListPresenter.View> extends Presenter<T> {

   protected List<User> users = new ArrayList<>();
   private boolean loading;
   private boolean finishedLoadingAllData;
   private int nextPage = 1;

   @Inject StartChatDelegate startChatDelegate;
   @Inject CirclesInteractor circlesInteractor;
   @Inject FriendsInteractor friendsInteractor;
   @Inject ProfileInteractor profileInteractor;

   @Override
   public void takeView(T view) {
      super.takeView(view);
      subscribeToChangingCircles();
      subscribeToRemovedFriends();
      if (isNeedPreload()) {
         reload();
      }
   }

   @VisibleForTesting
   public void subscribeToRemovedFriends() {
      friendsInteractor.getRemoveFriendPipe()
            .observeSuccess()
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(action -> userStateChanged(action.getResult()));
   }

   protected Observable<ActionState<GetCirclesCommand>> getCirclesObservable() {
      return circlesInteractor.getPipe()
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
      resetPaginationFields();
      view.startLoading();
      loadUsers(nextPage, this::onUsersLoaded);
   }

   protected void onUsersLoaded(List<User> freshUsers) {
      users.clear();
      processNewUsers(freshUsers);
   }

   protected void onUsersAdded(List<User> freshUsers) {
      processNewUsers(freshUsers);
   }

   @VisibleForTesting
   public void processNewUsers(List<User> freshUsers) {
      // server signals about end of pagination with empty page, NOT with items < page size
      finishedLoadingAllData = freshUsers == null || freshUsers.isEmpty();
      users.addAll(freshUsers);
      view.refreshUsers(users);
      view.finishLoading();
      loading = false;
      if (!finishedLoadingAllData) {
         nextPage++;
      }
   }

   protected void onError(GetUsersCommand getUsersCommand, Throwable throwable) {
      handleError(getUsersCommand, throwable);
      nextPage--;
      view.finishLoading();
      loading = false;
   }

   protected abstract void loadUsers(int page, rx.functions.Action1<List<User>> onSuccessAction);

   public void scrolled(int totalItemCount, int lastVisible) {
      if (!finishedLoadingAllData && !loading && lastVisible >= totalItemCount - 1) {
         view.startLoading();
         loading = true;
         loadUsers(nextPage, nextPage == 1 ? this::onUsersLoaded : this::onUsersAdded);
      }
   }

   private void resetPaginationFields() {
      nextPage = 1;
      finishedLoadingAllData = false;
      loading = false;
   }

   public void acceptRequest(User user) {
      getCirclesObservable().subscribe(new ActionStateSubscriber<GetCirclesCommand>().onStart(circlesCommand -> onCirclesStart())
            .onSuccess(circlesCommand -> onCirclesSuccessAcceptRequest(user, new ArrayList<>(circlesCommand.getResult())))
            .onFail(this::onCirclesError));
   }

   private void onCirclesSuccessAcceptRequest(User user, List<Circle> circles) {
      onCirclesSuccess(circles);
      view.showAddFriendDialog(circles, position ->
            friendsInteractor.getAcceptRequestPipe()
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
            .onSuccess(circlesCommand -> onCirclesSuccessAddUserRequest(user, new ArrayList<>(circlesCommand.getResult())))
            .onFail(this::onCirclesError));
   }

   private void onCirclesSuccessAddUserRequest(User user, List<Circle> circles) {
      onCirclesSuccess(circles);
      view.showAddFriendDialog(circles, arg -> addFriend(user, circles.get(arg)));
   }

   public void openPrefs(User user) {
      view.openFriendPrefs(new UserBundle(user));
   }

   public void startChat(User user) {
      startChatDelegate.startSingleChat(user, conversation -> MessengerActivity.startMessengerWithConversation(activityRouter
            .getContext(), conversation.getId()));
   }

   @VisibleForTesting
   public void subscribeToChangingCircles() {
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

   @VisibleForTesting
   public void userActionSucceed(User user) {
      if (view != null) {
         view.finishLoading();
         userStateChanged(user);
      }
   }

   protected abstract void userStateChanged(User user);

   public void unfriend(User user) {
      analyticsInteractor.analyticsActionPipe().send(FriendRelationshipAnalyticAction.unfriend());
      friendsInteractor.getRemoveFriendPipe()
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

   @VisibleForTesting
   public void addFriend(User user, Circle circle) {
      friendsInteractor.getAddFriendPipe()
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

   @VisibleForTesting
   public int getNextPage(){
      return nextPage;
   }

   @VisibleForTesting
   public List<User> getUsers(){
      return users;
   }

   @VisibleForTesting
   public void setUsers(List<User> users){
      this.users.clear();
      this.users.addAll(users);
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

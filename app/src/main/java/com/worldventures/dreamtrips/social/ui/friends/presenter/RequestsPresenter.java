package com.worldventures.dreamtrips.social.ui.friends.presenter;

import android.support.annotation.VisibleForTesting;

import com.innahema.collections.query.functions.Action1;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.model.Circle;
import com.worldventures.core.model.User;
import com.worldventures.core.ui.view.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.FriendsAnalyticsAction;
import com.worldventures.dreamtrips.social.service.friends.model.AcceptanceHeaderModel;
import com.worldventures.dreamtrips.social.service.friends.model.RequestHeaderModel;
import com.worldventures.dreamtrips.social.service.friends.interactor.CirclesInteractor;
import com.worldventures.dreamtrips.social.service.friends.interactor.FriendsInteractor;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.AcceptAllFriendRequestsCommand;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.DeleteFriendRequestCommand;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetCirclesCommand;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetRequestsCommand;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.profile.service.analytics.FriendRelationshipAnalyticAction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.worldventures.core.model.User.Relationship.INCOMING_REQUEST;
import static com.worldventures.core.model.User.Relationship.OUTGOING_REQUEST;
import static com.worldventures.core.model.User.Relationship.REJECTED;

public class RequestsPresenter extends Presenter<RequestsPresenter.View> {

   @Inject CirclesInteractor circlesInteractor;
   @Inject FriendsInteractor friendsInteractor;
   @Inject SnappyRepository snappyRepository;

   private int currentPage = 1;
   private int acceptedRequestsCount = 0;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      observeRequests();
      reloadRequests();
   }

   @VisibleForTesting
   public void observeRequests() {
      friendsInteractor.getRequestsPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetRequestsCommand>()
                  .onStart(getRequestsCommand -> view.startLoading())
                  .onFinish(getRequestsCommand -> view.finishLoading())
                  .onSuccess(this::requestsLoaded)
                  .onFail(this::handleError));
   }

   public void reloadRequests() {
      acceptedRequestsCount = 0;
      currentPage = 1;
      friendsInteractor.getRequestsPipe().send(new GetRequestsCommand(currentPage));
   }

   private void requestsLoaded(GetRequestsCommand getRequestsCommand) {
      currentPage++;
      showRequests(getRequestsCommand.items(), getRequestsCommand.isNoMoreElements());
   }

   private void showRequests(List<User> items, boolean noMoreElements) {
      List<Object> sortedItems = new ArrayList<>();
      List<User> incoming = getIncomingRequests(items);

      if (acceptedRequestsCount != 0 || !incoming.isEmpty()) {
         RequestHeaderModel incomingHeader = new RequestHeaderModel(context.getString(R.string.request_incoming_long), true);
         incomingHeader.setCount(incoming.size());
         sortedItems.add(incomingHeader);
      }

      if (acceptedRequestsCount != 0) {
         sortedItems.add(new AcceptanceHeaderModel(acceptedRequestsCount));
      } else if (!incoming.isEmpty()) {
         sortedItems.addAll(incoming);
      }

      List<User> outgoing = getOutgoingRequests(items);
      if (!outgoing.isEmpty()) {
         sortedItems.add(new RequestHeaderModel(context.getString(R.string.request_outgoing_long)));
         sortedItems.addAll(outgoing);
      }

      view.itemsLoaded(sortedItems, noMoreElements);
   }

   private List<User> getIncomingRequests(List<User> items) {
      return Queryable.from(items).filter(item -> item.getRelationship() == INCOMING_REQUEST).toList();
   }

   private List<User> getOutgoingRequests(List<User> items) {
      return Queryable.from(items)
            .filter(item -> (item.getRelationship() == OUTGOING_REQUEST || item.getRelationship() == REJECTED))
            .toList();
   }

   private int getFriendsRequestsCount() {
      return snappyRepository.getFriendsRequestsCount();
   }

   public void loadNext() {
      friendsInteractor.getRequestsPipe().send(new GetRequestsCommand(currentPage));
   }

   public void userClicked(User user) {
      view.openUser(new UserBundle(user));
   }

   @VisibleForTesting
   public Observable<ActionState<GetCirclesCommand>> getCirclesObservable() {
      return circlesInteractor.getPipe()
            .createObservable(new GetCirclesCommand())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView());
   }

   public void acceptAllRequests() {
      getCirclesObservable()
            .subscribe(new ActionStateSubscriber<GetCirclesCommand>().onStart(circlesCommand -> onCirclesStart())
                  .onSuccess(circlesCommand -> acceptAllCirclesSuccess(new ArrayList<>(circlesCommand.getResult())))
                  .onFail(this::onCirclesError));
   }

   private void acceptAllCirclesSuccess(List<Circle> circles) {
      view.hideBlockingProgress();
      view.showAddFriendDialog(circles, position ->
            friendsInteractor.getAcceptAllPipe()
                  .createObservable(new AcceptAllFriendRequestsCommand(circles.get(position).getId()))
                  .compose(bindView())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new ActionStateSubscriber<AcceptAllFriendRequestsCommand>()
                        .onStart(action -> view.startLoading())
                        .onFinish(action -> view.finishLoading())
                        .onSuccess(action -> allFriendRequestsAccepted())
                        .onFail(this::handleError))
      );
   }

   public void acceptRequest(User user) {
      getCirclesObservable()
            .subscribe(new ActionStateSubscriber<GetCirclesCommand>().onStart(circlesCommand -> onCirclesStart())
                  .onSuccess(circlesCommand -> acceptCirclesSuccess(user, new ArrayList<>(circlesCommand.getResult())))
                  .onFail(this::onCirclesError));
   }

   private void acceptCirclesSuccess(User user, List<Circle> circles) {
      view.hideBlockingProgress();
      view.showAddFriendDialog(circles, position ->
            friendsInteractor.getAcceptRequestPipe()
                  .createObservable(new ActOnFriendRequestCommand.Accept(user, circles.get(position).getId()))
                  .compose(bindView())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new ActionStateSubscriber<ActOnFriendRequestCommand.Accept>()
                        .onStart(action -> view.startLoading())
                        .onFinish(action -> view.finishLoading())
                        .onSuccess(action -> {
                           onSuccess(user);
                           updateRequestsCount();
                        })
                        .onFail(this::handleError))
      );
   }

   public void rejectRequest(User user) {
      analyticsInteractor.analyticsActionPipe().send(FriendRelationshipAnalyticAction.rejectRequest());
      friendsInteractor.getRejectRequestPipe()
            .createObservable(new ActOnFriendRequestCommand.Reject(user))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<ActOnFriendRequestCommand.Reject>()
                  .onStart(action -> view.startLoading())
                  .onFinish(action -> view.finishLoading())
                  .onSuccess(action -> {
                     onSuccess(user);
                     updateRequestsCount();
                  })
                  .onFail(this::handleError));
   }

   public void hideRequest(User user) {
      deleteRequest(user, DeleteFriendRequestCommand.Action.HIDE);
   }

   public void cancelRequest(User user) {
      analyticsInteractor.analyticsActionPipe().send(FriendRelationshipAnalyticAction.cancelRequest());
      deleteRequest(user, DeleteFriendRequestCommand.Action.CANCEL);
   }

   @VisibleForTesting
   public void deleteRequest(User user, DeleteFriendRequestCommand.Action actionType) {
      friendsInteractor.getDeleteRequestPipe()
            .createObservable(new DeleteFriendRequestCommand(user, actionType))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<DeleteFriendRequestCommand>()
                  .onStart(action -> view.startLoading())
                  .onFinish(action -> view.finishLoading())
                  .onSuccess(action -> onSuccess(user))
                  .onFail(this::handleError));
   }

   private void onSuccess(User user) {
      view.finishLoading();
      view.getAdapter().remove(user);
   }

   @VisibleForTesting
   public void allFriendRequestsAccepted() {
      acceptedRequestsCount = getFriendsRequestsCount();
      List<User> outgoingUsers = Queryable.from(view.getAdapter().getItems())
            .filter(item -> item instanceof User)
            .map(item -> (User) item)
            .filter(item -> (item.getRelationship() == OUTGOING_REQUEST || item.getRelationship() == REJECTED))
            .toList();
      showRequests(outgoingUsers, true);
   }

   private void updateRequestsCount() {
      if (view.getAdapter().getItem(0) instanceof RequestHeaderModel) {
         RequestHeaderModel model = ((RequestHeaderModel) view.getAdapter().getItem(0));
         model.setCount(Queryable.from(view.getAdapter().getItems())
               .count(item -> item instanceof User && ((User) item).getRelationship() == INCOMING_REQUEST));
         view.getAdapter().notifyItemChanged(0);
      }
   }

   private void onCirclesStart() {
      view.showBlockingProgress();
   }

   private void onCirclesError(CommandWithError commandWithError, Throwable throwable) {
      view.hideBlockingProgress();
      handleError(commandWithError, throwable);
   }

   public void onAddFriendsPressed() {
      analyticsInteractor.analyticsActionPipe().send(FriendsAnalyticsAction.addFriends());
      analyticsInteractor.analyticsActionPipe().send(FriendsAnalyticsAction.searchFriends());
   }

   @VisibleForTesting()
   public int getCurrentPage() {
      return currentPage;
   }

   public interface View extends Presenter.View, BlockingProgressView {
      void startLoading();

      void openUser(UserBundle userBundle);

      void finishLoading();

      void itemsLoaded(List<Object> sortedItems, boolean noMoreElements);

      void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectAction);

      BaseArrayListAdapter<Object> getAdapter();
   }
}

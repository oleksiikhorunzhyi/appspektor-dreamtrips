package com.worldventures.dreamtrips.modules.feed.presenter;

import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.service.NotificationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.BaseGetFeedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetNotificationsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationsAsReadCommand;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class NotificationPresenter extends Presenter<NotificationPresenter.View> {

   @Inject SnappyRepository db;
   @Inject NotificationFeedInteractor feedInteractor;
   @Inject NotificationCountEventDelegate notificationCountEventDelegate;

   public NotificationPresenter() {
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      notificationCountEventDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(event -> view.setRequestsCount(db.getFriendsRequestsCount()));
      subscribeToNotificationUpdates();
   }

   public void reload() {
      refreshFeed();
   }

   public void refreshRequestsCount() {
      view.setRequestsCount(db.getFriendsRequestsCount());
   }

   public void onRefresh() {
      refreshFeed();
   }

   private void refreshFeed() {
      view.startLoading();
      feedInteractor.notificationsPipe().send(GetNotificationsCommand.refresh());
   }

   private void subscribeToNotificationUpdates() {
      feedInteractor.notificationsPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetNotificationsCommand>()
                  .onProgress((action, progress) -> view.refreshNotifications(action.getItems()))
                  .onFail(this::notificationsError)
                  .onSuccess(action -> notificationsSucceed(action.getItems(), action.getResult())));
   }

   private void notificationsError(BaseGetFeedCommand action, Throwable throwable) {
      view.informUser(action.getErrorMessage());
      view.updateLoadingStatus(false, false);
      view.finishLoading();
   }


   private void notificationsSucceed(List<FeedItem<FeedEntity>> items, List<FeedItem<FeedEntity>> newItems) {
      boolean noMoreItems = newItems == null || newItems.size() == 0;
      view.updateLoadingStatus(false, noMoreItems);
      view.finishLoading();
      view.refreshNotifications(items);
      //
      if (!noMoreItems) feedInteractor.markNotificationsPipe().send(new MarkNotificationsAsReadCommand(newItems));
   }


   public void loadNext() {
      feedInteractor.notificationsPipe()
            .send(GetNotificationsCommand.loadMore());
      TrackingHelper.loadMoreNotifications();
   }

   public interface View extends RxView {

      void setRequestsCount(int count);

      void startLoading();

      void finishLoading();

      void refreshNotifications(List<FeedItem<FeedEntity>> notifications);

      void updateLoadingStatus(boolean loading, boolean noMoreElements);
   }
}

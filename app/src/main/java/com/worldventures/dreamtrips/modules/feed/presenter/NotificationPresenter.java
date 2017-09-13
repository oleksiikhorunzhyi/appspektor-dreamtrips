package com.worldventures.dreamtrips.modules.feed.presenter;

import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.service.NotificationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.analytics.LoadMoreNotificationsAnalyticAction;
import com.worldventures.dreamtrips.modules.feed.service.analytics.ViewNotificationScreenAnalyticAction;
import com.worldventures.dreamtrips.modules.feed.service.command.GetNotificationsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationsAsReadCommand;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class NotificationPresenter extends Presenter<NotificationPresenter.View> {

   @Inject SnappyRepository db;
   @Inject NotificationFeedInteractor feedInteractor;
   @Inject NotificationCountEventDelegate notificationCountEventDelegate;

   @Override
   public void onResume() {
      super.onResume();
      analyticsInteractor.analyticsActionPipe().send(new ViewNotificationScreenAnalyticAction());
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

   private void notificationsError(CommandWithError action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false, false);
      view.finishLoading();
   }

   private void notificationsSucceed(List<FeedItem> items, List<FeedItem> newItems) {
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
      analyticsInteractor.analyticsActionPipe().send(new LoadMoreNotificationsAnalyticAction());
   }

   public interface View extends RxView {

      void setRequestsCount(int count);

      void startLoading();

      void finishLoading();

      void refreshNotifications(List<FeedItem> notifications);

      void updateLoadingStatus(boolean loading, boolean noMoreElements);
   }
}

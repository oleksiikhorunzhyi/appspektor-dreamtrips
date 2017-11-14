package com.worldventures.dreamtrips.social.ui.feed.presenter;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor;
import com.worldventures.dreamtrips.modules.common.command.NotificationCountChangedCommand;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.service.NotificationFeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.LoadMoreNotificationsAnalyticAction;
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.ViewNotificationScreenAnalyticAction;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetNotificationsCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.MarkNotificationsAsReadCommand;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class NotificationPresenter extends Presenter<NotificationPresenter.View> {

   @Inject NotificationFeedInteractor feedInteractor;
   @Inject UserNotificationInteractor userNotificationInteractor;

   @Override
   public void onResume() {
      super.onResume();
      analyticsInteractor.analyticsActionPipe().send(new ViewNotificationScreenAnalyticAction());
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      userNotificationInteractor.notificationCountChangedPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> view.setRequestsCount(command.getFriendNotificationCount()));
      subscribeToNotificationUpdates();
   }

   public void reload() {
      refreshFeed();
   }

   public void refreshRequestsCount() {
      userNotificationInteractor.notificationCountChangedPipe().send(new NotificationCountChangedCommand());
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
      if (!noMoreItems) {
         feedInteractor.markNotificationsPipe().send(new MarkNotificationsAsReadCommand(newItems));
      }
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

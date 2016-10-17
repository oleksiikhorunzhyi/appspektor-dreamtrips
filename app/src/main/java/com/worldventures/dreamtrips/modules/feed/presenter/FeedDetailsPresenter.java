package com.worldventures.dreamtrips.modules.feed.presenter;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class FeedDetailsPresenter<V extends FeedDetailsPresenter.View> extends BaseCommentPresenter<V> {

   private static final String TAG = FeedItemDetailsPresenter.class.getSimpleName();

   protected FeedItem feedItem;

   private WeakHandler handler = new WeakHandler();

   @Inject FeedEntityManager entityManager;
   @Inject TripsInteractor tripsInteractor;

   public FeedDetailsPresenter(FeedItem feedItem) {
      super(feedItem.getItem());
      this.feedItem = feedItem;
   }

   @Override
   public void takeView(V view) {
      super.takeView(view);
      view.setFeedItem(feedItem);
      subscribeForTripsDetails();
      loadFullEventInfo();
   }

   @Override
   public void onInjected() {
      super.onInjected();
      entityManager.setRequestingPresenter(this);
   }

   @Override
   public void dropView() {
      super.dropView();
      handler.removeCallbacksAndMessages(null);
   }

   //todo until Trip becomes as all normal entities
   public boolean isTrip() {
      return feedItem instanceof TripFeedItem;
   }

   @Override
   protected boolean isNeedCheckCommentsWhenStart() {
      return isTrip();
   }

   private void loadFullEventInfo() {
      //TODO trip details is requested from other place, all this hierarchy should be refactored
      if (!isTrip()) doRequest(new GetFeedEntityQuery(feedEntity.getUid()),
            feedEntityHolder -> updateFullEventInfo(feedEntityHolder.getItem()),
            spiceException -> {
               Timber.e(spiceException, TAG);
               handleError(spiceException);
            });
   }

   protected void updateFullEventInfo(FeedEntity updatedFeedEntity) {
      feedEntity = updatedFeedEntity;
      feedEntity.setComments(null);
      feedItem.setItem(feedEntity);
      eventBus.post(new FeedEntityChangedEvent(feedEntity));
      checkCommentsAndLikesToLoad();
      view.updateFeedItem(feedItem);
      view.showAdditionalInfo(feedEntity.getOwner());
   }

   private void subscribeForTripsDetails() {
      tripsInteractor.detailsPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetTripDetailsCommand>()
                  .onSuccess(command -> updateFullEventInfo(command.getResult())));
   }

   public void onEvent(FeedEntityChangedEvent event) {
      if (event.getFeedEntity().equals(feedItem.getItem())) {
         feedItem.setItem(event.getFeedEntity());
         feedEntity = event.getFeedEntity();
         view.updateFeedItem(feedItem);
      }
   }

   public void onEvent(LikesPressedEvent event) {
      if (view.isVisibleOnScreen()) {
         FeedEntity model = event.getModel();
         if (!model.isLiked()) {
            entityManager.like(model);
         } else {
            entityManager.unlike(model);
         }
      }
   }

   public void onEvent(EntityLikedEvent event) {
      feedEntity.syncLikeState(event.getFeedEntity());
      eventBus.post(new FeedEntityChangedEvent(feedEntity));
   }

   public void onEvent(FeedEntityCommentedEvent event) {
      if (event.getFeedEntity().equals(feedItem.getItem())) {
         feedItem.setItem(event.getFeedEntity());
         feedEntity = event.getFeedEntity();
         view.updateFeedItem(feedItem);
      }
   }

   public interface View extends BaseCommentPresenter.View {

      void setFeedItem(FeedItem feedItem);

      void updateFeedItem(FeedItem feedItem);

      void showAdditionalInfo(User user);
   }
}

package com.worldventures.dreamtrips.modules.feed.presenter.delegate;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FlagDelegate;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DownloadImageCommand;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.functions.Action2;

public class FeedActionHandlerDelegate {

   private FeedInteractor feedInteractor;
   private FlagDelegate flagDelegate;
   private TripImagesInteractor tripImagesInteractor;

   public FeedActionHandlerDelegate(FeedInteractor feedInteractor, FlagsInteractor flagsInteractor,
         TripImagesInteractor tripImagesInteractor) {
      this.feedInteractor = feedInteractor;
      this.flagDelegate = new FlagDelegate(flagsInteractor);
      this.tripImagesInteractor = tripImagesInteractor;
   }

   public void onLikeItem(FeedItem feedItem) {
      feedInteractor.changeFeedEntityLikedStatusPipe()
            .send(new ChangeFeedEntityLikedStatusCommand(feedItem.getItem()));

      String id = feedItem.getItem().getUid();
      FeedEntityHolder.Type type = feedItem.getType();
      if (type != FeedEntityHolder.Type.UNDEFINED && !feedItem.getItem().isLiked()) {
         //send this event only if user likes item, if dislikes - skip
         TrackingHelper.sendActionItemFeed(TrackingHelper.ATTRIBUTE_LIKE, id, type);
      }
   }

   public void onLoadFlags(Flaggable flaggableView, Action2<Command, Throwable> errorAction) {
      flagDelegate.loadFlags(flaggableView, errorAction);
   }

   public void onFlagItem(String uid, int flagReasonId, String reason, FlagDelegate.View view,
         Action2<Command, Throwable> errorAction) {
      flagDelegate.flagItem(new FlagData(uid, flagReasonId, reason), view, errorAction);
   }

   public void onDownloadImage(String url, Observable.Transformer stopper, Action2<Command, Throwable> errorAction) {
      tripImagesInteractor.downloadImageActionPipe()
            .createObservable(new DownloadImageCommand(url))
            .compose(stopper)
            .subscribe(new ActionStateSubscriber<DownloadImageCommand>()
                  .onFail(errorAction::call));
   }
}

package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.feed.service.NotificationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationAsReadCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImageType;
import com.worldventures.dreamtrips.modules.tripsimages.model.VideoMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImageArgsFilterFunc;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.BaseMediaCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.TripImagesCommandFactory;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesArgs;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesFullscreenArgs;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;

public class TripImagesViewPagerPresenter extends BaseImageViewPagerPresenter<BaseImageViewPagerPresenter.View> {
   @Inject TripImagesCommandFactory tripImagesCommandFactory;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject NotificationFeedInteractor notificationFeedInteractor;

   List<BaseMediaEntity> baseMediaEntities;
   TripImagesArgs tripImagesArgs;
   int notificationId;

   public TripImagesViewPagerPresenter(TripImagesFullscreenArgs args) {
      super(args.isLastPageReached(), args.getCurrentItem());
      this.baseMediaEntities = args.getMediaEntityList();
      this.tripImagesArgs = args.getTripImagesArgs();
      this.notificationId = args.getNotificationId();
   }

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      subscribeToTripImages();
      if (notificationId != 0) {
         notificationFeedInteractor.markNotificationPipe().send(new MarkNotificationAsReadCommand(notificationId));
      }
   }

   @Override
   protected void initItems() {
      if (baseMediaEntities == null) {
         tripImagesInteractor.baseTripImagesCommandActionPipe()
               .createObservableResult(tripImagesCommandFactory.provideCommandCacheOnly(tripImagesArgs))
               .map(Command::getResult)
               .subscribe(items -> {
                  baseMediaEntities = items;
                  super.initItems();
               });
      } else {
         super.initItems();
      }
   }

   void subscribeToTripImages() {
      if (tripImagesArgs == null) return;
      tripImagesInteractor.baseTripImagesCommandActionPipe()
            .observe()
            .filter(new TripImageArgsFilterFunc(tripImagesArgs))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<BaseMediaCommand>()
                  .onStart(command -> loading = true)
                  .onFail((getYSBHPhotosCommand, throwable) -> {
                     loading = false;
                     handleError(getYSBHPhotosCommand, throwable);
                  })
                  .onSuccess(baseTripImagesCommand -> {
                     loading = false;
                     baseMediaEntities.addAll(baseTripImagesCommand.getResult());
                     view.setItems(getItems());
                  })
            );
   }

   @Override
   protected List<FragmentItem> getItems() {
      List<FragmentItem> items =  Queryable.from(baseMediaEntities)
            .filter(element -> element.getType() != TripImageType.UNKNOWN)
            .map(entity -> {
               if (entity.getType() == TripImageType.PHOTO) {
                  return new FragmentItem(Route.SOCIAL_IMAGE_FULLSCREEN, "", ((PhotoMediaEntity) entity).getItem());
               } else {
                  return new FragmentItem(Route.SOCIAL_VIDEO_FULLSCREEN, "", ((VideoMediaEntity) entity).getItem());
               }
            })
            .toList();
      return items;
   }

   @Override
   protected int getCurrentItemsSize() {
      return baseMediaEntities.size();
   }

   @Override
   protected void loadMore() {
      if (tripImagesArgs != null) {
         tripImagesInteractor.baseTripImagesCommandActionPipe()
               .send(tripImagesCommandFactory.provideLoadMoreCommand(tripImagesArgs, baseMediaEntities));
      }
   }

}

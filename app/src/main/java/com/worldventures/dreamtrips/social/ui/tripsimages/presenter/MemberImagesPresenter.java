package com.worldventures.dreamtrips.social.ui.tripsimages.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.CheckVideoProcessingStatusCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesAddedCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.delegate.MediaRefresher;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class MemberImagesPresenter extends TripImagesPresenter {

   @Inject MediaRefresher memberImagesRefresher;

   public MemberImagesPresenter(TripImagesArgs tripImagesArgs) {
      super(tripImagesArgs);
   }

   @Override
   public void onResume() {
      super.onResume();
      subscribeToRefresher();
   }

   @Override
   public void onPause() {
      super.onPause();
      stopRefreshingMemberImages();
   }

   void subscribeToRefresher() {
      tripImagesInteractor.baseTripImagesCommandActionPipe().observeSuccess()
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe(getMembersPhotosCommand -> startRefreshingMemberImages());
      memberImagesRefresher.getNewPhotosObservable()
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe(this::processNewPhotosFromRefresher,
                  throwable -> Timber.w(throwable, "Could not refresh trip images"));
      if (!loading) startRefreshingMemberImages();
   }

   public void onShowNewImagesClick() {
      List<BaseMediaEntity> lastPhotos = new ArrayList<>(memberImagesRefresher.getLastPhotos());
      if (lastPhotos.size() < tripImagesArgs.getPageSize()) {
         tripImagesInteractor.memberImagesAddedCommandPipe()
               .send(new MemberImagesAddedCommand(tripImagesArgs, lastPhotos));
         currentItems.addAll(0, lastPhotos);
         tripImagesInteractor.checkVideoProcessingStatusPipe()
               .send(new CheckVideoProcessingStatusCommand(currentItems));
         updateItemsInView();
         restartImageRefreshing();
      } else {
         reload();
         stopRefreshingMemberImages();
      }
      view.hideNewImagesButton();
   }

   private void startRefreshingMemberImages() {
      if (memberImagesAreRefreshing) return;
      memberImagesAreRefreshing = true;
      memberImagesRefresher.startRefreshing(Observable.defer(() -> {
         if (currentItems.isEmpty()) return Observable.empty();
         return Observable.just(currentItems.get(0));
      }), tripImagesArgs);
   }

   private void stopRefreshingMemberImages() {
      memberImagesRefresher.stopRefreshing();
      memberImagesAreRefreshing = false;
   }

   private void restartImageRefreshing() {
      stopRefreshingMemberImages();
      startRefreshingMemberImages();
   }

   private void processNewPhotosFromRefresher(List<BaseMediaEntity> newPhotos) {
      boolean uploading = Queryable.from(compoundOperationModels)
            .filter(element -> element.state() == CompoundOperationState.STARTED
                  || element.state() == CompoundOperationState.FINISHED)
            .count() > 0;
      if (loading || uploading) return;
      int newPhotosCount = Queryable.from(newPhotos).filter(photo -> !currentItems.contains(photo)).count();
      if (newPhotosCount > 0) {
         String newImagesString;
         String photosCountString = String.valueOf(newPhotosCount);
         if (newPhotosCount >= tripImagesArgs.getPageSize()) {
            newImagesString = context.getString(R.string.member_images_new_items_plus, photosCountString);
         } else if (newPhotosCount == 1) {
            newImagesString = context.getString(R.string.member_images_new_item, photosCountString);
         } else {
            newImagesString = context.getString(R.string.member_images_new_items, photosCountString);
         }
         view.showNewImagesButton(newImagesString);
      }
   }
}

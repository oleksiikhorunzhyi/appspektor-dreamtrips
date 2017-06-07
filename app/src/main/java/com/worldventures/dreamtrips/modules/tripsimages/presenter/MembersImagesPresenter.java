package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetMembersPhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.ImmutablePaginationParams;
import com.worldventures.dreamtrips.modules.tripsimages.service.delegate.MemberImagesRefresher;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import rx.Observable;
import timber.log.Timber;

public class MembersImagesPresenter extends MembersImagesBasePresenter<GetMembersPhotosCommand> {

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject MemberImagesRefresher memberImagesRefresher;

   private boolean memberImagesAreRefreshing;

   public MembersImagesPresenter() {
      this(TripImagesType.MEMBERS_IMAGES, 0);
   }

   public MembersImagesPresenter(TripImagesType type, int userId) {
      super(type, userId);
   }

   @Override
   public void onResume() {
      super.onResume();
      subscribeToNewImages();
   }

   @Override
   public void onPause() {
      super.onPause();
      stopRefreshingMemberImages();
   }

   private void subscribeToNewImages() {
      if (view.isFullscreenView()) return;
      getLoadingPipe().observeSuccess()
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe(getMembersPhotosCommand -> startRefreshingMemberImages());
      memberImagesRefresher.getNewPhotosObservable()
            .compose(bindViewToMainComposer())
            .subscribe(this::processNewPhotosFromRefresher,
                  throwable -> Timber.w(throwable, "Could not refresh trip images"));
      if (!loading) startRefreshingMemberImages();
   }

   @Override
   public void reload(boolean userInitiated) {
      super.reload(userInitiated);
      stopRefreshingMemberImages();
      view.hideNewImagesButton();
   }

   @Override
   protected void onFullDataLoaded(List<IFullScreenObject> items) {
      super.onFullDataLoaded(items);
      restartImageRefreshing();
   }

   private void processNewPhotosFromRefresher(List<Photo> newPhotos) {
      boolean uploading = Queryable.from(postUploads)
            .filter(element -> element.state() == CompoundOperationState.STARTED
                  || element.state() == CompoundOperationState.FINISHED)
            .count() > 0;
      if (loading || uploading) return;
      int newPhotosCount = Queryable.from(newPhotos).filter(photo -> !photos.contains(photo)).count();
      if (newPhotosCount > 0) {
         String newImagesString;
         String photosCountString = String.valueOf(newPhotosCount);
         if (newPhotosCount >= getPageSize()) {
            newImagesString = context.getString(R.string.member_images_new_items_plus, photosCountString);
         } else if (newPhotosCount == 1) {
            newImagesString = context.getString(R.string.member_images_new_item, photosCountString);
         } else {
            newImagesString = context.getString(R.string.member_images_new_items, photosCountString);
         }
         view.showNewImagesButton(newImagesString);
      }
   }

   private void restartImageRefreshing() {
      stopRefreshingMemberImages();
      startRefreshingMemberImages();
   }

   private void startRefreshingMemberImages() {
      if (memberImagesAreRefreshing) return;
      memberImagesAreRefreshing = true;
      memberImagesRefresher.startRefreshing(Observable.defer(() -> {
         if (photos.isEmpty()) return Observable.empty();
         return Observable.just((Photo) photos.get(0));
      }), getPageSize());
   }

   private void stopRefreshingMemberImages() {
      memberImagesRefresher.stopRefreshing();
      memberImagesAreRefreshing = false;
   }

   public void onShowNewImagesClick() {
      List<Photo> lastPhotos = memberImagesRefresher.getLastPhotos();
      if (lastPhotos.size() < getPageSize()) {
         photos.addAll(0, lastPhotos);
         view.addAll(0, new ArrayList<>(lastPhotos));
         restartImageRefreshing();
      } else {
         reload(true);
         stopRefreshingMemberImages();
      }
      view.hideNewImagesButton();
   }

   protected ActionPipe<GetMembersPhotosCommand> getLoadingPipe() {
      return tripImagesInteractor.getMembersPhotosPipe();
   }

   @Override
   protected GetMembersPhotosCommand getReloadCommand() {
      return new GetMembersPhotosCommand(ImmutablePaginationParams.builder().perPage(getPageSize()).build());
   }

   @Override
   protected GetMembersPhotosCommand getLoadMoreCommand(int currentCount) {
      Photo photo = (Photo) photos.get(photos.size() - 1);
      return new GetMembersPhotosCommand(ImmutablePaginationParams.builder()
            .perPage(getPageSize()).before(photo.getCreatedAt()).build());
   }

   @Override
   protected int getPageSize() {
      return 40;
   }

   @Override
   protected int getVisibleThreshold() {
      return 15;
   }
}

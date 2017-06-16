package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedEntityHolderDelegate;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImagesTabViewAnalyticsEvent;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.CommandWithTripImages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action1;

public abstract class TripImagesListPresenter<VT extends TripImagesListPresenter.View, C extends CommandWithTripImages>
      extends Presenter<VT> implements FeedEntityHolder {

   private static final int PER_PAGE = 15;
   private static final int VISIBLE_THRESHOLD = 5;

   @Inject SnappyRepository db;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject FeedInteractor feedInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject FeedEntityHolderDelegate feedEntityHolderDelegate;

   protected TripImagesType type;

   private int previousTotal = 0;
   protected boolean loading = true;
   private int currentPhotoPosition = 0;
   boolean lastPageReached = false;

   protected List<IFullScreenObject> photos = new ArrayList<>();
   protected int userId;

   private int currentPage;

   protected TripImagesListPresenter(TripImagesType type, int userId) {
      super();
      this.type = type;
      this.userId = userId;
   }

   public static TripImagesListPresenter create(TripImagesType type, int userId, ArrayList<IFullScreenObject> photos,
         int currentPhotosPosition, int notificationId) {
      TripImagesListPresenter presenter;
      switch (type) {
         case MEMBERS_IMAGES:
            presenter = new MembersImagesPresenter();
            break;
         case ACCOUNT_IMAGES:
         case ACCOUNT_IMAGES_FROM_PROFILE:
            presenter = new AccountImagesPresenter(type, userId);
            break;
         case YOU_SHOULD_BE_HERE:
            presenter = new YSBHPresenter(userId);
            break;
         case INSPIRE_ME:
            presenter = new InspireMePresenter(userId);
            break;
         case FIXED:
            presenter = new FixedListPhotosPresenter(photos, userId, notificationId);
            break;
         default:
            throw new RuntimeException("Trip image type is not found");
      }

      presenter.setCurrentPhotoPosition(currentPhotosPosition);
      return presenter;
   }

   public void onSelectedFromPager() {
      analyticsInteractor.analyticsActionPipe().send(TripImagesTabViewAnalyticsEvent.forTripImages(type));
   }

   @Override
   public void takeView(VT view) {
      super.takeView(view);
      view.clear();
      fillWithItems();
      if (!view.isFullscreenView()) {
         reload(false);
      }
      subscribeToNewItems();
      subscribeToPhotoDeletedEvents();
      subscribeToLikesChanges();
      subscribeToErrorUpdates();
      feedEntityHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer(), this::handleError);
   }

   private void fillWithItems() {
      photos.addAll(db.readPhotoEntityList(type, userId));
      refreshImagesInView();
      view.setSelection(currentPhotoPosition);
   }

   public void scrolled(int visibleItemCount, int totalItemCount, int firstVisibleItem) {
      if (totalItemCount > previousTotal) {
         loading = false;
         previousTotal = totalItemCount;
      }
      if (!lastPageReached && !loading && photos.size() > 0
            && (totalItemCount - visibleItemCount) <= (firstVisibleItem + getVisibleThreshold())) {
         loadNext();
      }
   }

   protected int getPageSize() {
      return PER_PAGE;
   }

   protected int getVisibleThreshold() {
      return VISIBLE_THRESHOLD;
   }

   protected abstract ActionPipe<C> getLoadingPipe();

   protected abstract C getReloadCommand();

   protected abstract C getLoadMoreCommand(int currentCount);

   public void reload(boolean userInitiated) {
      loading = true;
      view.startLoading();
      currentPage = 1;
      load(getReloadCommand(), this::onFullDataLoaded);
   }

   public void loadNext() {
      loading = true;
      currentPage++;
      load(getLoadMoreCommand(photos.size()), newPhotos -> {
         if (newPhotos.size() == 0) {
            lastPageReached = true;
         }
         savePhotosAndUpdateView(newPhotos);
      });
   }

   private void load(C command, Action1<List<IFullScreenObject>> successAction) {
      getLoadingPipe().createObservable(command)
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<C>()
                  .onSuccess(c -> {
                     loading = false;
                     successAction.call(c.getImages());
                  })
                  .onFail((failedCommand, throwable) -> {
                     loading = false;
                     view.finishLoading();
                     if (currentPage != 1) currentPage--;
                     super.handleError(failedCommand, throwable);
                  }));
   }

   protected void onFullDataLoaded(List<IFullScreenObject> items) {
      resetCurrentPhotosAndLoadingState();
      savePhotosAndUpdateView(items);
      view.setSelection(0);
   }

   private void resetCurrentPhotosAndLoadingState() {
      view.finishLoading();
      photos.clear();
      previousTotal = 0;
      lastPageReached = false;
      loading = false;
   }

   private void savePhotosAndUpdateView(List<IFullScreenObject> list) {
      photos.addAll(list);
      db.savePhotoEntityList(type, userId, photos);
      refreshImagesInView();
   }

   protected void refreshImagesInView() {
      view.setImages(photos);
   }

   public IFullScreenObject getPhoto(int position) {
      return photos.get(position);
   }

   public void setCurrentPhotoPosition(int currentPhotoPosition) {
      this.currentPhotoPosition = currentPhotoPosition;
   }

   public void onItemClick(IFullScreenObject image) {
      view.openFullscreen(getFullscreenArgs(photos.indexOf(image)).build());
   }

   private FullScreenImagesBundle.Builder getFullscreenArgs(int position) {
      return new FullScreenImagesBundle.Builder().position(position)
            .userId(userId)
            .route(getRouteByType(type))
            .type(type);
   }

   private Route getRouteByType(TripImagesType type) {
      switch (type) {
         case ACCOUNT_IMAGES:
         case MEMBERS_IMAGES:
         case FIXED:
            return Route.SOCIAL_IMAGE_FULLSCREEN;
         case INSPIRE_ME:
            return Route.INSPIRE_PHOTO_FULLSCREEN;
         case YOU_SHOULD_BE_HERE:
            return Route.YSBH_FULLSCREEN;
         default:
            return Route.SOCIAL_IMAGE_FULLSCREEN;
      }
   }

   private void subscribeToLikesChanges() {
      feedInteractor.changeFeedEntityLikedStatusPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<ChangeFeedEntityLikedStatusCommand>()
                  .onSuccess(this::itemLiked)
                  .onFail(this::handleError));
   }

   private void itemLiked(ChangeFeedEntityLikedStatusCommand command) {
      for (Object o : photos) {
         if (o instanceof Photo && ((Photo) o).getFSId().equals(command.getResult().getUid())) {
            ((Photo) o).syncLikeState(command.getResult());
            break;
         }
      }
   }

   private void subscribeToPhotoDeletedEvents() {
      tripImagesInteractor.deletePhotoPipe()
            .observeSuccessWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(deletePhotoCommand -> {
               tripImagesInteractor.deletePhotoPipe().clearReplays();
               for (int i = 0; i < photos.size(); i++) {
                  IFullScreenObject o = photos.get(i);
                  if (o.getFSId().equals(deletePhotoCommand.getResult())) {
                     photos.remove(i);
                     view.remove(i);
                     db.savePhotoEntityList(type, userId, photos);
                  }
               }
            });
   }

   private void subscribeToErrorUpdates() {
      offlineErrorInteractor.offlineErrorCommandPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> reportNoConnection());
   }

   @Override
   public void updateFeedEntity(FeedEntity updatedFeedEntity) {
      if (updatedFeedEntity instanceof Photo) {
         Photo temp = (Photo) updatedFeedEntity;
         int index = photos.indexOf(temp);

         if (index != -1) {
            photos.set(index, temp);
            db.savePhotoEntityList(type, userId, photos);
            view.setImages(photos);
         }
      }
   }

   @Override
   public void deleteFeedEntity(FeedEntity deletedFeedEntity) {
      if (deletedFeedEntity instanceof Photo) {
         Photo temp = (Photo) deletedFeedEntity;
         int index = photos.indexOf(temp);

         if (index != -1) {
            photos.remove(index);
            db.savePhotoEntityList(type, userId, photos);
            view.setImages(photos);
         }
      }
   }

   private void subscribeToNewItems() {
      postsInteractor.postCreatedPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .map(PostCreatedCommand::getFeedItem)
            .subscribe(this::onFeedItemAdded);
   }

   protected void onFeedItemAdded(FeedItem feedItem) {
      List<Photo> photosToAdd = new ArrayList<>();

      if (feedItem.getItem() instanceof Photo) {
         Photo photo = (Photo) feedItem.getItem();
         if (!photos.contains(photo)) {
            photosToAdd.add(photo);
         }
      } else if (feedItem.getItem() instanceof TextualPost && ((TextualPost) feedItem
            .getItem()).getAttachments().size() > 0) {
         List<Photo> feedItemPhotos = Queryable.from(((TextualPost) feedItem.getItem()).getAttachments())
               .map(holder -> (Photo) holder.getItem())
               .filter(photo -> !photos.contains(photo))
               .toList();
         boolean allPhotosHavePublishAt = Queryable.from(feedItemPhotos)
               .count(element -> element.getCreatedAt() == null) == 0;
         if (allPhotosHavePublishAt) {
            Collections.sort(feedItemPhotos, (p1, p2) -> p1.getCreatedAt().before(p2.getCreatedAt()) ? 1 : -1);
         } else {
            Collections.reverse(photos);
         }
         photosToAdd.addAll(feedItemPhotos);
      }

      if (!photosToAdd.isEmpty()) {
         photos.addAll(0, photosToAdd);
         db.savePhotoEntityList(type, userId, photos);
         if (photosToAdd.size() == 1) {
            view.add(0, photosToAdd.get(0));
         } else {
            view.addAll(0, photosToAdd);
         }
      }
   }

   public interface View extends RxView, AdapterView<IFullScreenObject> {

      void startLoading();

      void finishLoading();

      void setSelection(int photoPosition);

      void setImages(List<IFullScreenObject> items);

      void openFullscreen(FullScreenImagesBundle data);

      boolean isFullscreenView();
   }
}

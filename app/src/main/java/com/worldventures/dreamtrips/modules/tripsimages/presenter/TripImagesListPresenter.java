package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.ListAdapter;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.TripImagesCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action1;

public abstract class TripImagesListPresenter<VT extends TripImagesListPresenter.View, C extends TripImagesCommand<? extends IFullScreenObject>> extends Presenter<VT> {

   public static final int PER_PAGE = 15;
   public final static int VISIBLE_TRESHOLD = 5;

   @Inject SnappyRepository db;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject FeedInteractor feedInteractor;

   protected TripImagesType type;

   private int previousTotal = 0;
   private boolean loading = true;
   private int currentPhotoPosition = 0;

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

   @Override
   public void takeView(VT view) {
      super.takeView(view);
      view.clear();
      fillWithItems();
      if (!view.isFullscreenView()) {
         reload();
      }
      subscribeToPhotoDeletedEvents();
      subscribeToLikesChanges();
   }

   private void fillWithItems() {
      photos.addAll(db.readPhotoEntityList(type, userId));
      view.fillWithItems(photos);
      view.setSelection(currentPhotoPosition);
   }

   public void scrolled(int visibleItemCount, int totalItemCount, int firstVisibleItem) {
      if (totalItemCount > previousTotal) {
         loading = false;
         previousTotal = totalItemCount;
      }
      if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_TRESHOLD)) {
         loadNext();
         loading = true;
      }
   }

   protected abstract ActionPipe<C> getLoadingPipe();

   protected abstract C getReloadCommand();

   protected abstract C getLoadMoreCommand(int currentCount);

   public void reload() {
      view.startLoading();
      currentPage = 1;
      load(getReloadCommand(), this::onFullDataLoaded);
   }

   public void loadNext() {
      currentPage++;
      load(getLoadMoreCommand(view.getAdapter().getCount()), this::savePhotosAndUpdateView);
   }

   private void load(C command, Action1<List<IFullScreenObject>> successAction) {
      getLoadingPipe().createObservable(command)
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<C>()
                  .onSuccess(c -> successAction.call((List<IFullScreenObject>) c.getResult()))
                  .onFail((failedCommand, throwable) -> {
                     view.finishLoading();
                     if (currentPage != 1) currentPage--;
                     super.handleError(failedCommand, throwable);
                  }));
   }

   private void onFullDataLoaded(List<IFullScreenObject> items) {
      resetCurrentPhotosAndLoadingState();
      savePhotosAndUpdateView(items);
   }

   private void resetCurrentPhotosAndLoadingState() {
      view.finishLoading();
      photos.clear();
      previousTotal = 0;
      loading = false;
      view.getAdapter().clear();
   }

   private void savePhotosAndUpdateView(List<IFullScreenObject> list) {
      photos.addAll(list);
      db.savePhotoEntityList(type, userId, photos);
      view.getAdapter().addItems(list);
      view.getAdapter().notifyDataSetChanged();

   }

   public IFullScreenObject getPhoto(int position) {
      return photos.get(position);
   }

   public void setCurrentPhotoPosition(int currentPhotoPosition) {
      this.currentPhotoPosition = currentPhotoPosition;
   }

   public void onItemClick(int position) {
      view.openFullscreen(getFullscreenArgs(position).build());
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

   ////////////////////////////
   /// Events
   ////////////////////////////

   public void onEvent(FeedEntityChangedEvent event) {
      if (event.getFeedEntity() instanceof Photo) {
         Photo temp = (Photo) event.getFeedEntity();
         int index = photos.indexOf(temp);

         if (index != -1) {
            photos.set(index, temp);
            db.savePhotoEntityList(type, userId, photos);
         }
      }
   }

   public void onEventMainThread(FeedItemAddedEvent event) {
      if (event.getFeedItem().getItem() instanceof Photo) {
         Photo photo = (Photo) event.getFeedItem().getItem();
         photos.add(0, photo);
         db.savePhotoEntityList(type, userId, photos);
         view.add(0, photo);
      } else if (event.getFeedItem().getItem() instanceof TextualPost && ((TextualPost) event.getFeedItem()
            .getItem()).getAttachments().size() > 0) {
         List<Photo> addedPhotos = Queryable.from(((TextualPost) event.getFeedItem().getItem()).getAttachments())
               .map(holder -> (Photo) holder.getItem())
               .toList();
         Collections.reverse(addedPhotos);
         photos.addAll(0, addedPhotos);
         db.savePhotoEntityList(type, userId, photos);
         view.addAll(0, addedPhotos);
      }
   }

   public interface View extends RxView, AdapterView<IFullScreenObject> {

      void startLoading();

      void finishLoading();

      void setSelection(int photoPosition);

      void fillWithItems(List<IFullScreenObject> items);

      ListAdapter getAdapter();

      void openFullscreen(FullScreenImagesBundle data);

      void inject(Object getMyPhotosQuery);

      boolean isFullscreenView();
   }
}

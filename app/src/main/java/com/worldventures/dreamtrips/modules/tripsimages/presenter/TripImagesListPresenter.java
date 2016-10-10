package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.AccountImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.MembersImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImageViewAnalyticsEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public abstract class TripImagesListPresenter<VT extends TripImagesListPresenter.View> extends Presenter<VT> {

   public static final int PER_PAGE = 15;
   public final static int VISIBLE_TRESHOLD = 5;

   @Inject protected SnappyRepository db;

   protected TripImagesType type;
   protected boolean fullscreenMode;

   private int previousTotal = 0;
   private boolean loading = true;
   private int currentPhotoPosition = 0;

   protected List<IFullScreenObject> photos = new ArrayList<>();
   protected int userId;

   protected TripImagesListPresenter(TripImagesType type, int userId) {
      super();
      this.type = type;
      this.userId = userId;
   }

   public static TripImagesListPresenter create(TripImagesType type, int userId, ArrayList<IFullScreenObject> photos, boolean fullScreenMode, int currentPhotosPosition, int notificationId) {
      TripImagesListPresenter presenter;
      switch (type) {
         /**
          * ALL MEMBERS PHOTOS
          */
         case MEMBERS_IMAGES:
            presenter = new MembersImagesPresenter();
            break;
         case ACCOUNT_IMAGES:
         case ACCOUNT_IMAGES_FROM_PROFILE:
            presenter = new AccountImagesPresenter(TripImagesType.ACCOUNT_IMAGES, userId);
            break;
         case YOU_SHOULD_BE_HERE:
            presenter = new YSBHPresenter(userId);
            break;
         case INSPIRE_ME:
            presenter = new InspireMePresenter(userId);
            break;
         case FIXED:
            presenter = new FixedListPhotosFullScreenPresenter(photos, userId, notificationId);
            break;
         default:
            throw new RuntimeException("Trip image type is not found");
      }

      presenter.setFullscreenMode(fullScreenMode);
      presenter.setCurrentPhotoPosition(currentPhotosPosition);
      return presenter;
   }

   @Override
   public void takeView(VT view) {
      super.takeView(view);
      view.clear();
      syncPhotosAndUpdatePosition();
      view.fillWithItems(photos);
      view.setSelection(currentPhotoPosition);

      if (!fullscreenMode) {
         reload();
      }
   }

   protected void syncPhotosAndUpdatePosition() {
      photos.addAll(db.readPhotoEntityList(type, userId));

      if (fullscreenMode) {
         int prevPhotosCount = photos.size();
         photos = Queryable.from(photos).filter(element -> !(element instanceof UploadTask)).toList();
         currentPhotoPosition -= prevPhotosCount - photos.size();
      }
   }

   public void setFullscreenMode(boolean isFullscreen) {
      this.fullscreenMode = isFullscreen;
   }

   public void scrolled(int visibleItemCount, int totalItemCount, int firstVisibleItem) {
      if (totalItemCount > previousTotal) {
         loading = false;
         previousTotal = totalItemCount;
      }
      if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_TRESHOLD) && totalItemCount % PER_PAGE == 0) {
         loadNext();
         loading = true;
      }
   }

   private void loadNext() {
      if (dreamSpiceManager == null || getNextPageRequest(view.getAdapter().getCount()) == null) return;
      //
      doRequest(getNextPageRequest(view.getAdapter().getCount()), list -> {
         photos.addAll(list);
         db.savePhotoEntityList(type, userId, Queryable.from(photos)
               .filter(item -> !(item instanceof UploadTask))
               .toList());
         //
         view.getAdapter().addItems((ArrayList) list);
         view.getAdapter().notifyDataSetChanged();
      });
   }

   public void reload() {
      view.startLoading();
      doRequest(getReloadRequest(), items -> {
         view.finishLoading();
         //
         photos.clear();
         photos.addAll(items);
         resetLazyLoadFields();
         db.savePhotoEntityList(type, userId, Queryable.from(photos)
               .filter(item -> !(item instanceof UploadTask))
               .toList());
         //
         view.getAdapter().clear();
         view.getAdapter().addItems((ArrayList) photos);
         view.getAdapter().notifyDataSetChanged();
      });
   }

   @Override
   public void handleError(SpiceException error) {
      view.finishLoading();
      loading = false;
      super.handleError(error);
   }

   protected abstract SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount);

   protected abstract SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest();

   public IFullScreenObject getPhoto(int position) {
      return photos.get(position);
   }

   public void setCurrentPhotoPosition(int currentPhotoPosition) {
      this.currentPhotoPosition = currentPhotoPosition;
   }

   public void onItemClick(int position) {
      if (position != -1) {
         if (this instanceof MembersImagesPresenter) {
            IFullScreenObject screenObject = photos.get(position);
            analyticsInteractor.analyticsActionPipe().send(new TripImageViewAnalyticsEvent(screenObject.getFSId()));
         }
         int uploadTasksCount = Queryable.from(photos).count(item -> item instanceof UploadTask);
         view.openFullscreen(getFullscreenArgs(position - uploadTasksCount).build());
      }
   }

   @NonNull
   protected FullScreenImagesBundle.Builder getFullscreenArgs(int position) {
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

   private void resetLazyLoadFields() {
      previousTotal = 0;
      loading = false;
   }

   ////////////////////////////
   /// Events
   ////////////////////////////

   public void onEvent(EntityLikedEvent event) {
      for (Object o : photos) {
         if (o instanceof Photo && ((Photo) o).getFSId().equals(event.getFeedEntity().getUid())) {
            ((Photo) o).syncLikeState(event.getFeedEntity());
         }
      }
   }

   public void onEventMainThread(PhotoDeletedEvent event) {
      for (int i = 0; i < photos.size(); i++) {
         IFullScreenObject o = photos.get(i);
         if (o.getFSId().equals(event.getPhotoId())) {
            photos.remove(i);
            view.remove(i);
            db.savePhotoEntityList(type, userId, photos);
         }
      }
   }

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

      IRoboSpiceAdapter getAdapter();

      void openFullscreen(FullScreenImagesBundle data);

      void inject(Object getMyPhotosQuery);
   }
}

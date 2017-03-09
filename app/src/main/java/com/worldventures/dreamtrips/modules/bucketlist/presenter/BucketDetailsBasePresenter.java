package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketCoverBody;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class BucketDetailsBasePresenter<V extends BucketDetailsBasePresenter.View<T>, T> extends Presenter<V> {
   @Inject protected SnappyRepository db;

   @Inject protected BucketInteractor bucketInteractor;

   @State BucketItem.BucketType type;

   @State int ownerId;

   @State BucketItem bucketItem;

   public BucketDetailsBasePresenter(BucketBundle bundle) {
      type = bundle.getType();
      bucketItem = bundle.getBucketItem();
      ownerId = bundle.getOwnerId();
   }

   @Override
   public void onResume() {
      super.onResume();
      syncUI();
   }

   protected void syncUI() {
      if (bucketItem != null) {
         view.setTitle(bucketItem.getName());
         view.setDescription(bucketItem.getDescription());
         view.setStatus(bucketItem.isDone());
         view.setPeople(bucketItem.getFriends());
         view.setTags(bucketItem.getBucketTags());
         view.setTime(BucketItemInfoUtil.getTime(context, bucketItem));
      }
   }

   protected void putCoverPhotoAsFirst(List<BucketPhoto> photos) {
      if (!photos.isEmpty()) {
         int coverIndex = Math.max(photos.indexOf(bucketItem.getCoverPhoto()), 0);
         photos.get(coverIndex).setIsCover(true);
         photos.add(0, photos.remove(coverIndex));
      }
   }


   //////////////////////////////
   ///////// Photo processing
   //////////////////////////////

   /**
    * Current tab name will be override to null on BucketTypePresenter::dropView()
    *
    * @return
    * @see BucketDetailsBasePresenter#openFullScreen(int)
    * @see BucketTabsPresenter#dropView()
    */
   private boolean isTabTrulyVisible() {
      String currentTabTypeName = db.getOpenBucketTabType();
      return currentTabTypeName == null || currentTabTypeName.equalsIgnoreCase(type.getName());
   }


   /**
    * On Bucket List all instance of BucketDetailsFragment (with presenters) are initialized
    * and all of them receive callback from bus to openFullScreen.
    * It is not expected behaviour so I save current tab type onTabChange and
    * execute openFullScreen for truly visible tab.
    *
    * @param position
    * @see BucketTabsPresenter#onTabChange(com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType)
    * <p>
    * Method view.isVisibleOnScreen() cannot help to resolve this issue, because it returns
    * true for any BucketDetails instance (current tab and all others)
    * <p>
    * If this method calls from external code (so current type is null) -
    * isTabVisible will return true
    * @see BucketDetailsBasePresenter#isTabTrulyVisible()
    */
   public void openFullScreen(int position) {
      if (isTabTrulyVisible()) {
         TrackingHelper.actionBucketItemPhoto(TrackingHelper.ATTRIBUTE_VIEW_PHOTO, bucketItem.getUid());
         openFullScreen(bucketItem.getPhotos().get(position));
      }
   }

   public void openFullScreen(BucketPhoto selectedPhoto) {
      if ((bucketItem.getPhotos().contains(selectedPhoto)) && bucketItem.getOwner() != null) {
         ArrayList<IFullScreenObject> photos = new ArrayList<>();
         if (bucketItem.getCoverPhoto() != null) {
            Queryable.from(bucketItem.getPhotos()).forEachR(photo -> photo.setIsCover(photo.getFSId()
                  .equals(bucketItem.getCoverPhoto().getFSId())));
         }
         photos.addAll(bucketItem.getPhotos());

         FullScreenImagesBundle data = new FullScreenImagesBundle.Builder().position(photos.indexOf(selectedPhoto))
               .type(TripImagesType.FIXED)
               .route(Route.BUCKET_PHOTO_FULLSCREEN)
               .userId(bucketItem.getOwner().getId())
               .fixedList(photos)
               .foreign(bucketItem.getOwner().getId() != appSessionHolder.get().get().getUser().getId())
               .build();

         view.openFullscreen(data);
      }
   }

   public void saveCover(BucketPhoto photo) {
      view.bind(bucketInteractor.updatePipe()
            .createObservable(new UpdateBucketItemCommand(ImmutableBucketCoverBody.builder()
                  .id(bucketItem.getUid())
                  .status(bucketItem.getStatus())
                  .type(bucketItem.getType())
                  .coverId(photo.getFSId())
                  .build()))
            .observeOn(AndroidSchedulers.mainThread()))
            .subscribe(new ActionStateSubscriber<UpdateBucketItemCommand>().onSuccess(action -> {
               bucketItem = action.getResult();
               syncUI();
            }).onFail(this::handleError));
   }

   @Override
   public void dropView() {
      super.dropView();
   }

   public interface View<T> extends RxView {
      void setTitle(String title);

      void setDescription(String description);

      void setTime(String time);

      void setPeople(String people);

      void setTags(String tags);

      void setStatus(boolean isCompleted);

      void done();

      void openFullscreen(FullScreenImagesBundle data);

      void setImages(List<T> photos);
   }
}
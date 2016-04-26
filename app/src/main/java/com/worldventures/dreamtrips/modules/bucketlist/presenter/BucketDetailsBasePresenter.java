package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemPhotoAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class BucketDetailsBasePresenter<V extends BucketDetailsBasePresenter.View> extends Presenter<V> {

    @Inject
    BucketItemManager bucketItemManager;
    @Inject
    protected SnappyRepository db;

    protected BucketItem.BucketType type;
    protected String bucketItemId;
    protected int ownerId;

    protected BucketItem bucketItem;

    public BucketDetailsBasePresenter(BucketBundle bundle) {
        super();
        type = bundle.getType();
        bucketItemId = bundle.getBucketItemUid();
        ownerId = bundle.getOwnerId();
    }

    @Override
    public void onResume() {
        super.onResume();

        getBucketItemManager().setDreamSpiceManager(dreamSpiceManager);
        restoreBucketItem();

        syncUI();
    }

    protected void restoreBucketItem() {
        if (ownerId == 0) {
            bucketItem = getBucketItemManager().getBucketItem(type, bucketItemId);
        } else {
            bucketItem = getBucketItemManager().getSingleBucketItem(type, bucketItemId, ownerId);
        }
    }

    public void onEventMainThread(BucketItemUpdatedEvent event) {
        restoreBucketItem();
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

            List<BucketPhoto> photos = bucketItem.getPhotos();
            if (photos != null && !photos.isEmpty()) {
                int coverIndex = Math.max(photos.indexOf(bucketItem.getCoverPhoto()), 0);
                Collections.reverse(photos);
                Collections.swap(photos, coverIndex, 0);
                view.setImages(photos);
            }
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
            eventBus.post(new BucketItemPhotoAnalyticEvent(TrackingHelper.ATTRIBUTE_VIEW_PHOTO, bucketItemId));
            openFullScreen(bucketItem.getPhotos().get(position));
        }
    }

    public void openFullScreen(BucketPhoto selectedPhoto) {
        if ((bucketItem.getPhotos().contains(selectedPhoto))) {
            ArrayList<IFullScreenObject> photos = new ArrayList<>();
            if (bucketItem.getCoverPhoto() != null) {
                Queryable.from(bucketItem.getPhotos()).forEachR(photo ->
                        photo.setIsCover(photo.getFSId().equals(bucketItem.getCoverPhoto().getFSId())));
            }
            photos.addAll(bucketItem.getPhotos());

            FullScreenImagesBundle data = new FullScreenImagesBundle.Builder()
                    .position(photos.indexOf(selectedPhoto))
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
        getBucketItemManager().updateBucketItemCoverId(bucketItem, photo.getFSId(), this);
    }

    protected BucketItemManager getBucketItemManager() {
        return bucketItemManager;
    }

    @Override
    public void dropView() {
        super.dropView();
    }


    public interface View extends RxView {
        void setTitle(String title);

        void setDescription(String description);

        void setTime(String time);

        void setPeople(String people);

        void setTags(String tags);

        void setStatus(boolean isCompleted);

        void done();

        void openFullscreen(FullScreenImagesBundle data);

        void setImages(List<BucketPhoto> photos);

        UploadTask getBucketPhotoUploadTask(long taskId);
    }
}

package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.os.Handler;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.AmazonDelegate;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.events.InsertNewImageUploadTaskEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoLikeEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public abstract class TripImagesListPresenter<T extends IFullScreenObject> extends Presenter<TripImagesListPresenter.View>
        implements TransferListener {

    public static final int PER_PAGE = 15;
    public final static int VISIBLE_TRESHOLD = 5;

    @Inject
    protected SnappyRepository db;

    @Inject
    AmazonDelegate amazonDelegate;

    protected Type type;
    private boolean isFullscreen;

    private int previousTotal = 0;
    private boolean loading = true;

    private TripImagesRoboSpiceController roboSpiceAdapterController;
    private List<IFullScreenObject> photos = new ArrayList<>();

    public TripImagesListPresenter(Type type) {
        super();
        this.type = type;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        photos.clear();
        photos.addAll(db.readPhotoEntityList(type));
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.clear();
        view.fillWithItems(photos);
        view.setSelection();

        if (type != Type.BUCKET_PHOTOS && !isFullscreen)
            reload();
    }

    private void resetLazyLoadFields() {
        previousTotal = 0;
        loading = false;
    }

    public void scrolled(int visibleItemCount, int totalItemCount, int firstVisibleItem) {
        if (totalItemCount > previousTotal) {
            loading = false;
            previousTotal = totalItemCount;
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_TRESHOLD)
                && totalItemCount % PER_PAGE == 0) {
            getAdapterController().loadNext();
            loading = true;
        }
    }

    @Override
    public void dropView() {
        if (roboSpiceAdapterController != null)
            roboSpiceAdapterController.setAdapter(null);
        super.dropView();
    }

    public IFullScreenObject getPhoto(int position) {
        return photos.get(position);
    }

    public void onItemClick(int position) {
        if (position != -1) {
            IFullScreenObject obj = photos.get(position);
            if (obj instanceof ImageUploadTask) {
                if (((ImageUploadTask) obj).isFailed()) {
                    ((ImageUploadTask) obj).setFailed(false);
                    uploadPhoto((ImageUploadTask) obj);
                }
            } else {
                this.activityRouter.openFullScreenPhoto(position, type);
            }
        }
    }

    public void onEventMainThread(InsertNewImageUploadTaskEvent event) {
        if (type != Type.MY_IMAGES) {
            getAdapterController().reload();
        } else {
            uploadPhoto(event.getUploadTask());
        }
    }

    private void uploadPhoto(ImageUploadTask imageUploadTask) {
        TrackingHelper.photoUploadStarted(imageUploadTask.getType(), "");

        amazonDelegate.uploadTripPhoto(context, imageUploadTask).setTransferListener(this);
        db.saveUploadImageTask(imageUploadTask);

        photos.add(0, imageUploadTask);
        view.add(0, imageUploadTask);
        db.savePhotoEntityList(type, photos);
    }

    private void photoUploaded(int id) {
        ImageUploadTask task = (ImageUploadTask)
                Queryable.from(photos).firstOrDefault(photo -> photo instanceof ImageUploadTask &&
                        ((ImageUploadTask) photo).getAmazonTaskId() == id);


        if (task != null) {
            task.setOriginUrl(task.getAmazonResultUrl());
            doRequest(new AddTripPhotoCommand(task), photo -> {
                db.removeImageUploadTask(task);
                processPhoto(photos.indexOf(task), photo);
            });
        }

    }

    private void processPhoto(int index, Photo photo) {
        view.getAdapter().notifyDataSetChanged();

        photos.remove(index);
        photos.add(index, photo);
        db.savePhotoEntityList(type, photos);

        new Handler().postDelayed(() -> view.replace(index, photo), 300);
    }

    private void photoFailed(int id) {
        ImageUploadTask task = (ImageUploadTask)
                Queryable.from(photos).firstOrDefault(photo -> photo instanceof ImageUploadTask &&
                        ((ImageUploadTask) photo).getAmazonTaskId() == id);

        if (task != null) {
            task.setFailed(true);
            db.saveUploadImageTask(task);
            view.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onStateChanged(int id, TransferState state) {
        switch (state) {
            case COMPLETED:
                photoUploaded(id);
                break;
        }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
    }

    @Override
    public void onError(int id, Exception ex) {
        photoFailed(id);
    }

    public void onEvent(PhotoLikeEvent event) {
        for (Object o : photos) {
            if (o instanceof Photo && ((Photo) o).getFsId().equals(event.getId())) {
                ((Photo) o).setLiked(event.isLiked());
            }
        }
    }

    public void onEventMainThread(PhotoDeletedEvent event) {
        for (int i = 0; i < photos.size(); i++) {
            IFullScreenObject o = photos.get(i);
            if (o.getFsId().equals(event.getPhotoId())) {
                photos.remove(i);
                view.remove(i);
                db.savePhotoEntityList(type, photos);
            }
        }
    }

    private TripImagesRoboSpiceController getAdapterController() {
        if (roboSpiceAdapterController == null) {
            roboSpiceAdapterController = getTripImagesRoboSpiceController();
            roboSpiceAdapterController.setSpiceManager(dreamSpiceManager);
        }

        if (!roboSpiceAdapterController.hasAdapter()) {
            roboSpiceAdapterController.setAdapter(view.getAdapter());
        }
        return roboSpiceAdapterController;
    }

    public void reload() {
        getAdapterController().reload();
    }

    public abstract TripImagesRoboSpiceController getTripImagesRoboSpiceController();

    public void setFullscreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
    }

    public static TripImagesListPresenter create(Type type, boolean isFullscreen) {
        TripImagesListPresenter presenter = new MyImagesPresenter();
        switch (type) {
            case MEMBER_IMAGES:
                presenter = new UserImagesPresenter();
                break;
            case MY_IMAGES:
                presenter = new MyImagesPresenter();
                break;
            case YOU_SHOULD_BE_HERE:
                presenter = new YSBHPresenter();
                break;
            case INSPIRE_ME:
                presenter = new InspireMePresenter();
                break;
            case BUCKET_PHOTOS:
                presenter = new BucketPhotoFsPresenter();
                break;
        }
        presenter.setFullscreen(isFullscreen);
        return presenter;
    }

    public abstract class TripImagesRoboSpiceController extends DreamSpiceAdapterController<IFullScreenObject> {

        @Override
        public void onStart(LoadType loadType) {
            if (loadType == LoadType.RELOAD) {
                view.startLoading();
            }
        }

        @Override
        public void onFinish(RoboSpiceAdapterController.LoadType
                                     loadType, List<IFullScreenObject> items, SpiceException spiceException) {
            if (getAdapterController() != null) {
                view.finishLoading();
                if (spiceException == null) {
                    if (loadType == RoboSpiceAdapterController.LoadType.RELOAD) {

                        if (type.equals(Type.MY_IMAGES)) {
                            Queryable.from(amazonDelegate.getUploadingTransfers())
                                    .forEachR(transferObserver ->
                                            transferObserver.setTransferListener(TripImagesListPresenter.this));
                        }

                        photos.clear();
                        photos.addAll(items);
                        resetLazyLoadFields();
                    } else {
                        photos.addAll(items);
                    }

                    db.savePhotoEntityList(type, photos);
                } else {
                    handleError(spiceException);
                }
            }
        }
    }

    public interface View extends Presenter.View, AdapterView<IFullScreenObject> {
        void startLoading();

        void finishLoading();

        void setSelection();

        void fillWithItems(List<IFullScreenObject> items);

        IRoboSpiceAdapter getAdapter();

        void inject(Object getMyPhotos);
    }

}

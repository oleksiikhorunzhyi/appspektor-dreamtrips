package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.core.utils.events.InsertNewImageUploadTaskEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.trips.event.TripImageAnalyticEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripImageBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public abstract class TripImagesListPresenter
        extends Presenter<TripImagesListPresenter.View> implements TransferListener {

    public static final int PER_PAGE = 15;
    public final static int VISIBLE_TRESHOLD = 5;

    @Inject
    protected SnappyRepository db;

    protected Type type;
    private boolean fullscreenMode;

    private int previousTotal = 0;
    private boolean loading = true;
    private int currentPhotoPosition = 0;

    private TripImagesRoboSpiceController roboSpiceAdapterController;
    protected List<IFullScreenObject> photos = new ArrayList<>();

    protected int userId;

    protected TripImagesListPresenter(Type type, int userId) {
        super();
        this.type = type;
        this.userId = userId;
    }

    @Override
    public void onInjected() {
        super.onInjected();
    }

    protected void syncPhotosAndUpdatePosition() {
        photos.addAll(db.readPhotoEntityList(type, userId));

        if (fullscreenMode) {
            int prevPhotosCount = photos.size();
            photos = Queryable.from(photos).filter(element -> !(element instanceof UploadTask)).toList();
            currentPhotoPosition -= prevPhotosCount - photos.size();
        }
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.clear();
        syncPhotosAndUpdatePosition();
        view.fillWithItems(photos);
        view.setSelection(currentPhotoPosition);

        if (type != Type.FIXED_LIST && !fullscreenMode) reload();
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

    public void setCurrentPhotoPosition(int currentPhotoPosition) {
        this.currentPhotoPosition = currentPhotoPosition;
    }

    public void onEventMainThread(InsertNewImageUploadTaskEvent event) {
        if (type != Type.MY_IMAGES) {
            getAdapterController().reload();
        } else {
            savePhotoIfNeeded(event.getUploadTask());
        }
    }

    public void onItemClick(int position) {
        if (position != -1) {
            IFullScreenObject obj = photos.get(position);
            if (obj instanceof UploadTask) {
                if (((UploadTask) obj).getStatus().equals(UploadTask.Status.FAILED)) {
                    ((UploadTask) obj).setStatus(UploadTask.Status.IN_PROGRESS);

                    view.replace(photos.indexOf(obj), obj);

                    startUpload((UploadTask) obj);
                }
            } else {
                if (this instanceof UserImagesPresenter) {
                    IFullScreenObject screenObject = photos.get(position);
                    eventBus.post(new TripImageAnalyticEvent(screenObject.getFsId(), TrackingHelper.ATTRIBUTE_VIEW));
                }
                view.openFullscreen(getFullscreenArgs(position).build());
            }
        }
    }

    @NonNull
    protected FullScreenImagesBundle.Builder getFullscreenArgs(int position) {
        return new FullScreenImagesBundle.Builder()
                .position(position)
                .type(type);
    }

    private void savePhotoIfNeeded(UploadTask uploadTask) {
        doRequest(new CopyFileCommand(context, uploadTask.getFilePath()), filePath ->
                uploadPhoto(uploadTask, filePath));
    }

    private void uploadPhoto(UploadTask uploadTask, String filePath) {
        uploadTask.setFilePath(filePath);
        uploadTask.setStatus(UploadTask.Status.IN_PROGRESS);

        photos.add(0, uploadTask);
        view.add(0, uploadTask);
        db.savePhotoEntityList(type, userId, photos);
        startUpload(uploadTask);
    }

    private void startUpload(UploadTask uploadTask) {
        TrackingHelper.photoUploadStarted(uploadTask.getType(), "");
        TransferObserver transferObserver = photoUploadingSpiceManager.upload(uploadTask);
        uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));

        db.saveUploadTask(uploadTask);
        transferObserver.setTransferListener(this);
    }

    @Override
    public void onStateChanged(int id, TransferState state) {
        if (view != null) {
            UploadTask uploadTask = getCurrentTask(String.valueOf(id));
            if (uploadTask != null) {
                if (state.equals(TransferState.COMPLETED)) {
                    if (uploadTask.getStatus() != UploadTask.Status.COMPLETED) {
                        uploadTask.setStatus(UploadTask.Status.COMPLETED);
                        uploadTask.setOriginUrl
                                (photoUploadingSpiceManager.getResultUrl(uploadTask));
                        photoUploaded(uploadTask);
                    }
                } else if (state.equals(TransferState.FAILED)) {
                    photoError(getCurrentTask(String.valueOf(id)));
                }

                updateTask(uploadTask);
            }

        }
    }

    private void photoError(UploadTask uploadTask) {
        if (uploadTask != null) {
            uploadTask.setStatus(UploadTask.Status.FAILED);
            updateTask(uploadTask);
        }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
    }

    @Override
    public void onError(int id, Exception ex) {
        photoError(getCurrentTask(String.valueOf(id)));
    }

    private void photoUploaded(UploadTask task) {
        doRequest(new AddTripPhotoCommand(task), photo -> {
            processPhoto(photos.indexOf(task), photo);
            db.removeUploadTask(task);
        }, spiceException -> {
            photoError(getCurrentTask(task.getAmazonTaskId()));
        });
    }

    private void processPhoto(int index, Photo photo) {
        photos.set(index, photo);
        db.savePhotoEntityList(type, userId, photos);

        new Handler().postDelayed(() -> {
            if (view != null) view.replace(index, photo);
        }, 300);
    }

    private void updateTask(UploadTask task) {
        if (view == null) return;
        int index = photos.indexOf(task);
        view.replace(index, task);
    }

    private UploadTask getCurrentTask(String id) {
        return (UploadTask) Queryable.from(photos).firstOrDefault(item ->
                item instanceof UploadTask
                        && id.equals(((UploadTask) item).getAmazonTaskId()));

    }

    public void onEvent(EntityLikedEvent event) {
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
                db.savePhotoEntityList(type, userId, photos);
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

    public void setFullscreenMode(boolean isFullscreen) {
        this.fullscreenMode = isFullscreen;
    }

    public static TripImagesListPresenter create(TripImageBundle bundle) {
        TripImagesListPresenter presenter;
        switch (bundle.getType()) {
            case MEMBER_IMAGES:
                presenter = new UserImagesPresenter(bundle.getUserId());
                break;
            case MY_IMAGES:
                presenter = new AccountImagesPresenter(bundle.getUserId());
                break;
            case YOU_SHOULD_BE_HERE:
                presenter = new YSBHPresenter(bundle.getUserId());
                break;
            case INSPIRE_ME:
                presenter = new InspireMePresenter(bundle.getUserId());
                break;
            case FIXED_LIST:
                presenter = new FixedPhotoFsPresenter(bundle.getPhotos(), bundle.getUserId());
                break;
            case FOREIGN_IMAGES:
                presenter = new ForeignImagesPresenter(bundle.getUserId());
                break;
            default:
                throw new RuntimeException("Trip image type is not found");
        }
        presenter.setFullscreenMode(bundle.isFullScreenMode());
        presenter.setCurrentPhotoPosition(bundle.getCurrentPhotosPosition());
        return presenter;
    }

    public abstract class TripImagesRoboSpiceController extends DreamSpiceAdapterController<IFullScreenObject> {

        @Override
        public void onStart(LoadType loadType) {
            if (loadType.equals(LoadType.RELOAD)) {
                view.startLoading();
            }
        }

        @Override
        protected void onRefresh(ArrayList<IFullScreenObject> iFullScreenObjects) {
            prepareTasks(iFullScreenObjects);
            super.onRefresh(iFullScreenObjects);
        }

        @Override
        public void onPreFinish(RoboSpiceAdapterController.LoadType loadType,
                                List<IFullScreenObject> items, SpiceException spiceException) {
            if (getAdapterController() != null) {
                view.finishLoading();
                if (spiceException == null) {

                    if (loadType.equals(RoboSpiceAdapterController.LoadType.RELOAD)) {
                        photos.clear();
                        photos.addAll(items);
                        resetLazyLoadFields();
                    } else {
                        photos.addAll(items);
                    }

                    db.savePhotoEntityList(type, userId, photos);
                } else {
                    handleError(spiceException);
                }
            }
        }
    }

    private void prepareTasks(List<IFullScreenObject> items) {
        Queryable.from(items).forEachR(item -> {
            if (item instanceof UploadTask) prepareTask((UploadTask) item);
        });
    }

    private void prepareTask(UploadTask uploadTask) {
        TransferObserver transferObserver = photoUploadingSpiceManager
                .getTransferById(uploadTask.getAmazonTaskId());
        transferObserver.setTransferListener(this);
        onStateChanged(transferObserver.getId(), transferObserver.getState());
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

    public interface View extends Presenter.View, AdapterView<IFullScreenObject> {
        void startLoading();

        void finishLoading();

        void setSelection(int photoPosition);

        void fillWithItems(List<IFullScreenObject> items);

        IRoboSpiceAdapter getAdapter();

        void openFullscreen(FullScreenImagesBundle data);

        void inject(Object getMyPhotosQuery);
    }
}

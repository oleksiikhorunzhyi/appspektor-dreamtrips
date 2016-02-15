package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.worldventures.dreamtrips.core.api.PhotoUploadSubscriber;
import com.worldventures.dreamtrips.core.api.UploadPurpose;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.core.utils.events.InsertNewImageUploadTaskEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.trips.event.TripImageAnalyticEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddPhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.events.ImageUploadedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.AccountImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.MembersImagesPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public abstract class TripImagesListPresenter<VT extends TripImagesListPresenter.View>
        extends Presenter<VT> {

    public static final int PER_PAGE = 15;
    public final static int VISIBLE_TRESHOLD = 5;

    @Inject
    protected SnappyRepository db;

    protected TripImagesType type;
    private boolean fullscreenMode;

    private int previousTotal = 0;
    private boolean loading = true;
    private int currentPhotoPosition = 0;

    private TripImagesRoboSpiceController roboSpiceAdapterController;
    protected List<IFullScreenObject> photos = new ArrayList<>();
    private List<PhotoTag> photoTags;

    protected int userId;

    protected TripImagesListPresenter(TripImagesType type, int userId) {
        super();
        this.type = type;
        this.userId = userId;
    }

    public static TripImagesListPresenter create(TripImagesType type, int userId, ArrayList<IFullScreenObject> photos,
                                                 boolean fullScreenMode, int currentPhotosPosition, int notificationId) {
        TripImagesListPresenter presenter;
        switch (type) {
            /**
             * ALL MEMBERS PHOTOS
             */
            case MEMBERS_IMAGES:
                presenter = new MembersImagesPresenter();
                break;
            case ACCOUNT_IMAGES:
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

        PhotoUploadSubscriber.bind(view,
                photoUploadingManager.getTaskChangingObservable(UploadPurpose.TRIP_IMAGE))
                .onError(uploadTask -> {
                    photoError(getCurrentTask(uploadTask.getId()));
                })
                .onSuccess((task) -> {
                    if (!fullscreenMode) photoUploaded(task);
                })
                .onProgress(uploadTask -> {
                    int index = photos.indexOf(uploadTask);
                    if (index >= 0) updateTask(uploadTask);
                    else addTask(uploadTask);
                });
    }

    @Override
    public void dropView() {
        if (roboSpiceAdapterController != null)
            roboSpiceAdapterController.setAdapter(null);
        super.dropView();
    }

    protected void syncPhotosAndUpdatePosition() {
        photos.addAll(db.readPhotoEntityList(type, userId));

        if (fullscreenMode) {
            int prevPhotosCount = photos.size();
            photos = Queryable.from(photos).filter(element -> !(element instanceof UploadTask)).toList();
            currentPhotoPosition -= prevPhotosCount - photos.size();
        } else {
            photos.addAll(0, photoUploadingManager.getUploadTasks(UploadPurpose.TRIP_IMAGE));
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
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_TRESHOLD)
                && totalItemCount % PER_PAGE == 0) {
            getAdapterController().loadNext();
            loading = true;
        }
    }

    public IFullScreenObject getPhoto(int position) {
        return photos.get(position);
    }

    public void setCurrentPhotoPosition(int currentPhotoPosition) {
        this.currentPhotoPosition = currentPhotoPosition;
    }

    public void onItemClick(int position) {
        if (position != -1) {
            IFullScreenObject obj = photos.get(position);
            if (obj instanceof UploadTask) {
                if (((UploadTask) obj).getStatus().equals(UploadTask.Status.FAILED)) {
                    ((UploadTask) obj).setStatus(UploadTask.Status.STARTED);
                    view.replace(photos.indexOf(obj), obj);
                    uploadPhoto((UploadTask) obj);
                }
            } else {
                if (this instanceof MembersImagesPresenter) {
                    IFullScreenObject screenObject = photos.get(position);
                    eventBus.post(new TripImageAnalyticEvent(screenObject.getFSId(), TrackingHelper.ATTRIBUTE_VIEW));
                }
                int uploadTasksCount = Queryable.from(photos).count(item -> item instanceof UploadTask);
                view.openFullscreen(getFullscreenArgs(position - uploadTasksCount).build());
            }
        }
    }

    @NonNull
    protected FullScreenImagesBundle.Builder getFullscreenArgs(int position) {
        return new FullScreenImagesBundle.Builder()
                .position(position)
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

    protected void photoUploaded(UploadTask task) {

    }

    protected void uploadTags(String id) {
        if (photoTags == null || photoTags.isEmpty()) return;
        doRequest(new AddPhotoTagsCommand(id, photoTags));
    }

    protected void photoError(UploadTask uploadTask) {
        if (uploadTask != null) {
            uploadTask.setStatus(UploadTask.Status.FAILED);
            updateTask(uploadTask);
        }
    }

    protected void processPhoto(int index, Photo photo) {
        photos.set(index, photo);
        /**
         * Filter {@link UploadTask}, because sometimes after rotating device several times there were
         * some problems with tasks duplication.
         */
        db.savePhotoEntityList(type, userId, Queryable.from(photos)
                .filter(item -> !(item instanceof UploadTask)).toList());

        new Handler().postDelayed(() -> {
            if (view != null) view.replace(index, photo);
        }, 300);
    }

    private void addTask(UploadTask task) {
        if (view == null) return;
        photos.add(0, task);
        view.add(0, task);
    }

    private void updateTask(UploadTask task) {
        if (view == null) return;
        int index = photos.indexOf(task);
        view.replace(index, task);
    }

    protected UploadTask getCurrentTask(long id) {
        return (UploadTask) Queryable.from(photos).firstOrDefault(item ->
                item instanceof UploadTask
                        && id == (((UploadTask) item).getId()));

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

    //////////////////////////////////
    /// abstract Robospice controller
    //////////////////////////////////

    public abstract TripImagesRoboSpiceController getTripImagesRoboSpiceController();

    public abstract class TripImagesRoboSpiceController extends DreamSpiceAdapterController<IFullScreenObject> {

        @Override
        public void onStart(LoadType loadType) {
            if (loadType.equals(LoadType.RELOAD)) {
                view.startLoading();
            }
        }

        @Override
        protected void onRefresh(ArrayList<IFullScreenObject> iFullScreenObjects) {
            onPreFinish(LoadType.RELOAD, iFullScreenObjects, null);
            onFinish(LoadType.RELOAD, iFullScreenObjects, null);
        }

        @Override
        public void onPreFinish(RoboSpiceAdapterController.LoadType loadType,
                                List<IFullScreenObject> items, SpiceException spiceException) {
            if (getAdapterController() != null) {
                view.finishLoading();
                if (spiceException == null) {
                    if (loadType.equals(RoboSpiceAdapterController.LoadType.RELOAD)) {
                        UploadTask uploadTask = null;
                        if (photos.size() > 0 && photos.get(0) instanceof UploadTask)
                            uploadTask = (UploadTask) photos.get(0);
                        //
                        photos.clear();
                        if (uploadTask != null) photos.add(uploadTask);
                        photos.addAll(items);
                        resetLazyLoadFields();
                    } else {
                        photos.addAll(items);
                    }

                    db.savePhotoEntityList(type, userId, Queryable.from(photos)
                            .filter(item -> !(item instanceof UploadTask)).toList());
                } else {
                    handleError(spiceException);
                }
            }
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

    public void onEventMainThread(InsertNewImageUploadTaskEvent event) {
        this.photoTags = event.getPhotoTags();
        if (type == TripImagesType.ACCOUNT_IMAGES) {
            uploadPhoto(event.getUploadTask());
        }
    }

    private void uploadPhoto(UploadTask uploadTask) {
        TrackingHelper.photoUploadStarted(uploadTask.getType(), "");
        photoUploadingManager.upload(uploadTask, UploadPurpose.TRIP_IMAGE);
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

    public void onEventMainThread(ImageUploadedEvent event) {
        if (fullscreenMode) return;
        //
        if (event.isSuccess) {
            processPhoto(photos.indexOf(event.task), event.photo);
            uploadTags(event.photo.getFSId());
        } else {
            photoError(getCurrentTask(event.task.getId()));
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

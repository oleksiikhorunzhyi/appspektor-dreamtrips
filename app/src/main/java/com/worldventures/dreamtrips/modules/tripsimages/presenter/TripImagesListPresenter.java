package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.events.InsertNewImageUploadTaskEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoLikeEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFinished;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public abstract class TripImagesListPresenter<T extends IFullScreenObject> extends Presenter<TripImagesListPresenter.View> {

    public static final int PER_PAGE = 15;
    public final static int VISIBLE_TRESHOLD = 5;

    @Inject
    protected SnappyRepository db;

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
                    photoUploadSpiceManager.uploadPhoto((ImageUploadTask) obj);
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
            photos.add(0, event.getUploadTask());
            view.add(0, event.getUploadTask());
        }
        db.savePhotoEntityList(type, photos);
    }

    public void onEventMainThread(PhotoUploadFinished event) {
        if (type != Type.MY_IMAGES) {
            getAdapterController().reload();
        } else {
            for (int i = 0; i < photos.size(); i++) {
                Object item = photos.get(i);
                if (item instanceof ImageUploadTask && ((ImageUploadTask) item).getTaskId().equals(event.getPhoto().getTaskId())) {
                    photos.remove(i);
                    photos.add(i, event.getPhoto());
                    view.replace(i, event.getPhoto());
                    db.savePhotoEntityList(type, photos);
                    break;
                }
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

    public void onEvent(PhotoLikeEvent event) {
        for (Object o : photos) {
            if (o instanceof Photo && ((Photo) o).getFsId().equals(event.getId())) {
                ((Photo) o).setLiked(event.isLiked());
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
                        photos.clear();
                        photos.addAll(items);
                        resetLazyLoadFields();
                    } else {
                        photos.addAll(items);
                    }

                    db.savePhotoEntityList(type, photos);

                    for (IFullScreenObject item : items) {
                        if (item instanceof ImageUploadTask
                                && ((ImageUploadTask) item).isFailed()) {
                            photoUploadSpiceManager.uploadPhoto((ImageUploadTask) item);
                        }
                    }
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

package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.os.Handler;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.events.FSUploadEvent;
import com.worldventures.dreamtrips.core.utils.events.InsertNewImageUploadTaskEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoLikeEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFinished;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public abstract class TripImagesListPresenter<T extends IFullScreenAvailableObject> extends Presenter<TripImagesListPresenter.View> {

    public static final int PER_PAGE = 15;
    public final static int VISIBLE_TRESHOLD = 5;

    protected Type type;

    protected int firstVisibleItem;
    protected int visibleItemCount;
    protected int totalItemCount;

    private int previousTotal = 0;
    private boolean loading = true;
    private TripImagesRoboSpiceController roboSpiceAdapterController;

    public TripImagesListPresenter(Type type) {
        super();
        this.type = type;
    }

    public static TripImagesListPresenter create(Type type, View view) {
        switch (type) {
            case MEMBER_IMAGES:
                return new UserImagesPresenter();
            case MY_IMAGES:
                return new MyImagesPresenter();
            case YOU_SHOULD_BE_HERE:
                return new YSBHPM(view);
            case INSPIRE_ME:
                return new InspireMePresenter();
            case BUCKET_PHOTOS:
                return new BucketPhotoFsPresenter();
        }
        return new MyImagesPresenter();
    }

    public void onEventMainThread(FSUploadEvent.InspireMeImagesFSEvent event) {
        handleNewPhotoEvent(event);
    }

    public void onEventMainThread(FSUploadEvent.YSBHImagesFSEvent event) {
        handleNewPhotoEvent(event);
    }

    public void onEventMainThread(FSUploadEvent.MyImagesFSEvent event) {
        handleNewPhotoEvent(event);
    }

    public void onEventMainThread(FSUploadEvent.MemberImagesFSEvent event) {
        handleNewPhotoEvent(event);
    }

    public void onEventMainThread(FSUploadEvent.BucketPhotoFsEvent event) {
        handleNewPhotoEvent(event);
    }

    private void handleNewPhotoEvent(FSUploadEvent event) {
        new Handler().postDelayed(() -> {
            if (type == event.getType()
                    && view != null
                    && view.getAdapter() != null
                    && view.getAdapter().getCount() == 0) {
                view.clear();
                view.addAll(event.getImages());
                view.setSelection();
            }
        }, 100);
    }

    private void resetLazyLoadFields() {
        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        previousTotal = 0;
        loading = false;
    }

    public void scrolled(int childCount, int itemCount, int firstVisibleItemPosition) {
        visibleItemCount = childCount;
        totalItemCount = itemCount;
        firstVisibleItem = firstVisibleItemPosition;
        if (totalItemCount > previousTotal) {
            loading = false;
            previousTotal = totalItemCount;
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_TRESHOLD)
                && itemCount % PER_PAGE == 0) {
            getAdapterController().loadNext();
            loading = true;
        }
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (type != Type.BUCKET_PHOTOS)
            reload();
    }

    @Override
    public void dropView() {
        if (roboSpiceAdapterController != null)
            roboSpiceAdapterController.setAdapter(null);
        super.dropView();
    }

    public void onItemClick(int position) {
        if (position != -1) {
            List<IFullScreenAvailableObject> objects = view.getPhotosFromAdapter();
            IFullScreenAvailableObject obj = objects.get(position);
            if (obj instanceof ImageUploadTask) {
                if (((ImageUploadTask) obj).isFailed()) {
                    dreamSpiceManager.uploadPhoto((ImageUploadTask) obj);
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
            view.add(0, event.getUploadTask());
        }
        eventBus.postSticky(FSUploadEvent.create(type, view.getPhotosFromAdapter()));

    }

    public void onEventMainThread(PhotoUploadFinished event) {
        if (type != Type.MY_IMAGES) {
            getAdapterController().reload();
        } else {
            new Handler().postDelayed(() -> {
                for (int i = 0; i < view.getPhotosFromAdapter().size(); i++) {
                    Object item = view.getPhotosFromAdapter().get(i);
                    if (item instanceof ImageUploadTask && ((ImageUploadTask) item).getTaskId().equals(event.getPhoto().getTaskId())) {
                        view.replace(i, event.getPhoto());
                        eventBus.postSticky(FSUploadEvent.create(type, view.getPhotosFromAdapter()));
                        break;
                    }
                }

            }, 600);
        }
    }

    public void onEventMainThread(PhotoDeletedEvent event) {
        List<IFullScreenAvailableObject> photosFromAdapter = view.getPhotosFromAdapter();
        for (int i = 0; i < photosFromAdapter.size(); i++) {
            Object o = photosFromAdapter.get(i);
            if (o instanceof Photo && ((Photo) o).getFsId().equals(event.getPhotoId())) {
                view.remove(i);
                eventBus.postSticky(FSUploadEvent.create(type, view.getPhotosFromAdapter()));
            }
        }
    }

    public void onEvent(PhotoLikeEvent event) {
        for (Object o : view.getPhotosFromAdapter()) {
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

    public interface View extends Presenter.View, AdapterView<IFullScreenAvailableObject> {
        List<IFullScreenAvailableObject> getPhotosFromAdapter();

        void startLoading();

        void finishLoading();

        void setSelection();

        IRoboSpiceAdapter getAdapter();

        void inject(Object getMyPhotos);
    }

    public abstract class TripImagesRoboSpiceController extends DreamSpiceAdapterController<T> {

        @Override
        public void onStart(LoadType loadType) {
            if (loadType == LoadType.RELOAD) {
                view.startLoading();
            }
        }

        @Override
        public void onFinish(RoboSpiceAdapterController.LoadType
                                     loadType, List<T> items, SpiceException spiceException) {
            if (getAdapterController() != null) {
                view.finishLoading();
                if (spiceException == null) {
                    List<IFullScreenAvailableObject> list;
                    if (loadType == RoboSpiceAdapterController.LoadType.RELOAD) {
                        list = new ArrayList<>();
                        list.addAll(items);
                        resetLazyLoadFields();
                    } else {
                        list = new ArrayList<>(view.getPhotosFromAdapter());
                        for (Iterator<T> iterator = items.iterator(); iterator.hasNext(); ) {
                            T item = iterator.next();
                            if (list.contains(item)) {
                                iterator.remove();
                            }
                        }
                        list.addAll(items);
                    }

                    eventBus.postSticky(FSUploadEvent.create(type, list));

                    for (T item : items) {
                        if (item instanceof ImageUploadTask
                                && ((ImageUploadTask) item).isFailed()) {
                            dreamSpiceManager.uploadPhoto((ImageUploadTask) item);
                        }
                    }
                } else {
                    handleError(spiceException);
                }
            }
        }
    }

}

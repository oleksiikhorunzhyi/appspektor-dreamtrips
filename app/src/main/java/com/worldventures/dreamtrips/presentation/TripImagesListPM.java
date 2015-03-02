package com.worldventures.dreamtrips.presentation;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.presentation.tripimages.InspireMePM;
import com.worldventures.dreamtrips.presentation.tripimages.MyImagesPM;
import com.worldventures.dreamtrips.presentation.tripimages.UserImagesPM;
import com.worldventures.dreamtrips.presentation.tripimages.YSBHPM;
import com.worldventures.dreamtrips.utils.busevents.FSUploadEvent;
import com.worldventures.dreamtrips.utils.busevents.PhotoDeletedEvent;
import com.worldventures.dreamtrips.utils.busevents.PhotoLikeEvent;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadFinished;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadStarted;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

import static com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask.ImageUploadTaskFullscreen;
import static com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask.from;
import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

public abstract class TripImagesListPM<T extends IFullScreenAvailableObject> extends BasePresentation<TripImagesListPM.View> {

    public static final int PER_PAGE = 15;
    @Inject
    protected Context context;
    protected Type type;
    @Inject
    @Global
    EventBus eventBus;
    int firstVisibleItem, visibleItemCount, totalItemCount, llastPage;
    private RequestListener<ArrayList<T>> cbNext = new RequestListener<ArrayList<T>>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            view.finishLoading();
        }

        @Override
        public void onRequestSuccess(ArrayList<T> objects) {
            List<IFullScreenAvailableObject> list = new ArrayList<>(view.getPhotosFromAdapter());
            list.addAll(objects);
            eventBus.postSticky(FSUploadEvent.create(type, list));
            view.finishLoading();
        }
    };
    private RequestListener<ArrayList<T>> cbRefresh = new RequestListener<ArrayList<T>>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            view.finishLoading();
        }

        @Override
        public void onRequestSuccess(ArrayList<T> objects) {
            List<IFullScreenAvailableObject> list = new ArrayList<>();
            list.addAll(objects);
            resetLazyLoadFields();
            eventBus.postSticky(FSUploadEvent.create(type, list));
            view.finishLoading();
        }
    };
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;

    public TripImagesListPM(View view, Type type) {
        super(view);
        this.type = type;
    }

    public static TripImagesListPM create(Type type, View view) {
        switch (type) {
            case MEMBER_IMAGES:
                return new UserImagesPM(view);
            case MY_IMAGES:
                return new MyImagesPM(view);
            case YOU_SHOULD_BE_HERE:
                return new YSBHPM(view);
            case INSPIRE_ME:
                return new InspireMePM(view);
        }
        return null;
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

    private void handleNewPhotoEvent(FSUploadEvent event) {
        new Handler().postDelayed(() -> {
            if (type == event.getType()) {
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

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        int page = itemCount / PER_PAGE + 1;
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold) && itemCount % PER_PAGE == 0) {
            loadNext(page);
            loading = true;
        }
    }

    @Override
    public void init() {
        super.init();
        eventBus.registerSticky(this);
    }

    public void destroy() {
        eventBus.unregister(this);
    }

    public void reload() {
        reload(PER_PAGE);
    }

    public void reload(int perPage) {
        view.startLoading();
        loadMore(perPage, 1, cbRefresh);
    }

    public void loadNext(int page) {
        loadMore(PER_PAGE, page, cbNext);
        Log.d("LOAD INFO", page + " " + PER_PAGE + " ");
    }

    private void loadMore(int perPage, int page, RequestListener<ArrayList<T>> callback) {
        loadPhotos(perPage, page, callback);
    }

    public abstract void loadPhotos(int perPage, int page, RequestListener<ArrayList<T>> callback);

    public void onItemClick(int position) {
        List<IFullScreenAvailableObject> objects = view.getPhotosFromAdapter();
        List<IFullScreenAvailableObject> photos = new ArrayList<>();
        for (Object o : objects) {
            if (o instanceof IFullScreenAvailableObject) {
                photos.add((IFullScreenAvailableObject) o);
            } else if (o instanceof ImageUploadTask) {
                photos.add(from((ImageUploadTask) o));
            }
        }
        if (objects.get(position) instanceof IFullScreenAvailableObject) {
            this.activityRouter.openFullScreenPhoto(position, type);
        }
    }

    public void onEventMainThread(PhotoUploadStarted event) {
        if (type != Type.MY_IMAGES) {
            reload();
        } else {
            view.add(0, from(event.getUploadTask()));
        }
    }

    public void onEventMainThread(PhotoUploadFinished event) {
        if (type != Type.MY_IMAGES) {
            reload();
        } else {
            new Handler().postDelayed(() -> {
                for (int i = 0; i < view.getPhotosFromAdapter().size(); i++) {
                    Object item = view.getPhotosFromAdapter().get(i);
                    if (item instanceof ImageUploadTaskFullscreen && ((ImageUploadTaskFullscreen) item).getTask().getTaskId().equals(event.getPhoto().getTaskId())) {
                        view.replace(i, event.getPhoto());
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
            if (o instanceof Photo && ((Photo) o).getId() == event.getPhotoId()) {
                view.remove(i);
            }
        }
    }

    public void onEvent(PhotoLikeEvent event) {
        for (Object o : view.getPhotosFromAdapter()) {
            if (o instanceof Photo && ((Photo) o).getId() == event.getId()) {
                ((Photo) o).setLiked(event.isLiked());
            }
        }
    }

    public static interface View extends BasePresentation.View, AdapterView<IFullScreenAvailableObject> {
        List<IFullScreenAvailableObject> getPhotosFromAdapter();

        void startLoading();

        void finishLoading();

        void setSelection();
    }

}

package com.worldventures.dreamtrips.presentation;

import android.content.Context;
import android.os.Handler;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.presentation.tripimages.InspireMePM;
import com.worldventures.dreamtrips.presentation.tripimages.MyImagesPM;
import com.worldventures.dreamtrips.presentation.tripimages.UserImagesPM;
import com.worldventures.dreamtrips.presentation.tripimages.YSBHPM;
import com.worldventures.dreamtrips.utils.busevents.PhotoDeletedEvent;
import com.worldventures.dreamtrips.utils.busevents.PhotoLikeEvent;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadFinished;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadStarted;

import org.robobinding.annotation.PresentationModel;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

@PresentationModel
public abstract class TripImagesListPM<T> extends BasePresentation<TripImagesListPM.View> {

    public static final int PER_PAGE = 15;
    @Inject
    protected DreamTripsApi dreamTripsApi;

    @Inject
    protected Context context;

    @Inject
    @Global
    EventBus eventBus;

    protected Type type;
    private Callback<List<T>> cbNext;
    private Callback<List<T>> cbRefresh;
    private Callback<List<T>> cbPrev;


    public TripImagesListPM(View view, Type type) {
        super(view);
        this.type = type;
        cbNext = new Callback<List<T>>() {
            @Override
            public void success(List<T> objects, Response response) {
                view.addAll((List<Object>) objects);
                view.finishLoading();
            }

            @Override
            public void failure(RetrofitError error) {
                view.finishLoading();
            }
        };
        cbRefresh = new Callback<List<T>>() {
            @Override
            public void success(List<T> objects, Response response) {
                view.clear();//PullTORefresh
                view.addAll((List<Object>) objects);
                view.finishLoading();
            }

            @Override
            public void failure(RetrofitError error) {
                view.finishLoading();
            }
        };
        cbPrev = new Callback<List<T>>() {
            @Override
            public void success(List<T> objects, Response response) {
                view.addAll((List<Object>) objects);
                view.finishLoading();
            }

            @Override
            public void failure(RetrofitError error) {
                view.finishLoading();
            }
        };
    }

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

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
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            loadNext(itemCount / PER_PAGE + 1);
            loading = true;
        }
    }


    public interface Command {
        List<ImageUploadTask> run();
    }

    @Override
    public void init() {
        super.init();
        eventBus.register(this);
    }


    public void reload() {
        view.startLoading();
        loadMore(1, cbRefresh);
    }

    public void loadNext(int page) {
        loadMore(page, cbNext);
    }

    public void loadPrev(int page) {
        loadMore(page, cbPrev);
    }

    private void loadMore(int page, Callback<List<T>> callback) {
        loadPhotos(PER_PAGE, page, callback);
    }

    public abstract void loadPhotos(int perPage, int page, Callback<List<T>> callback);


    public void onItemClick(int position) {
        List<Object> photos = view.getPhotosFromAdapter();
        if (photos.get(position) instanceof Photo) {
            this.activityRouter.openFullScreenPhoto(photos, position, type);
        }
    }

    public static interface View extends BasePresentation.View, AdapterView<Object> {
        List<Object> getPhotosFromAdapter();

        void startLoading();

        void finishLoading();
    }


    public void onEventMainThread(PhotoUploadStarted event) {
        if (type != Type.MY_IMAGES) {
            reload();
        } else {
            view.add(0, event.getUploadTask());
        }
    }

    public void onEventMainThread(PhotoUploadFinished event) {
        if (type != Type.MY_IMAGES) {
            reload();
        } else {
            new Handler().postDelayed(() -> {
                for (int i = 0; i < view.getPhotosFromAdapter().size(); i++) {
                    Object item = view.getPhotosFromAdapter().get(i);
                    if (item instanceof ImageUploadTask && ((ImageUploadTask) item).getTaskId().equals(event.getPhoto().getTaskId())) {
                        view.replace(i, event.getPhoto());
                        break;
                    }
                }

            }, 600);
        }
    }

    public void onEventMainThread(PhotoDeletedEvent event) {
        List<Object> photosFromAdapter = view.getPhotosFromAdapter();
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

}

package com.worldventures.dreamtrips.presentation;

import android.content.Context;
import android.os.Handler;

import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.utils.busevents.PhotoLikeEvent;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadFinished;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadStarted;

import org.robobinding.annotation.PresentationModel;

import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

@PresentationModel
public abstract class TripImagesListFragmentPM<T> extends BasePresentation<TripImagesListFragmentPM.View> {

    public static final int PER_PAGE = 15;
    @Inject
    protected DreamTripsApi dreamTripsApi;

    @Inject
    protected Context context;

    protected Type type;
    private Callback<List<T>> callback;
    private int currentPage;

    public TripImagesListFragmentPM(View view, Type type) {
        super(view);
        this.type = type;
        callback = new Callback<List<T>>() {
            @Override
            public void success(List<T> objects, Response response) {
                if(currentPage==1){
                    view.clear();//PullTORefresh
                }
                currentPage++;
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
            // End has been reached

            // Do something

            loadPhotos(PER_PAGE, currentPage, callback);
            loading = true;
        }
    }

    public interface Command {
        List<ImageUploadTask> run();
    }

    @Override
    public void init() {
        super.init();
    }


    public void reload() {
        currentPage = 1;
        view.startLoading();
        loadPhotos(PER_PAGE, currentPage, callback);
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

    public void onEvent(PhotoLikeEvent event) {
        for (Object o : view.getPhotosFromAdapter()) {
            if (o instanceof Photo && ((Photo) o).getId() == event.getId()) {
                ((Photo) o).setLiked(event.isLiked());
            }
        }
    }

}

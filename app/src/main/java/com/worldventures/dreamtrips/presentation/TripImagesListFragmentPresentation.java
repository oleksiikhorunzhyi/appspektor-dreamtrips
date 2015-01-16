package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.model.response.ListPhotoResponse;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;

import org.robobinding.annotation.PresentationModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@PresentationModel
public class TripImagesListFragmentPresentation extends BasePresentation<TripImagesListFragmentPresentation.View> {

    @Inject
    DreamTripsApi dreamTripsApi;

    private TripImagesListFragment.Type type;
    private ArrayList<Photo> data;

    public TripImagesListFragmentPresentation(View view, TripImagesListFragment.Type type) {
        super(view);
        this.type = type;
    }

    final Callback<ListPhotoResponse> callback = new Callback<ListPhotoResponse>() {
        @Override
        public void success(ListPhotoResponse listPhotoResponse, Response response) {
            view.clearAdapter();
            data = listPhotoResponse.getData();
            view.setPhotos(data);
        }

        @Override
        public void failure(RetrofitError error) {
            view.setPhotos(null);
            handleError(error);
        }
    };

    public void loadImages() {
        switch (type) {
            case MY_IMAGES:
                final User user = appSessionHolder.get().get().getUser();
                dreamTripsApi.getMyPhotos(user.getId(), callback);
                break;
            case MEMBER_IMAGES:
                dreamTripsApi.getUserPhotos(callback);
                break;
            case YOU_SHOULD_BE_HERE:
                callback.success(new ListPhotoResponse(), null);
                break;
        }
    }

    public void onItemClick(int position) {
        activityRouter.openFullScreenPhoto(data, position);
    }

    public static interface View extends BasePresentation.View {
        void setPhotos(List<Photo> photos);

        void clearAdapter();
    }
}

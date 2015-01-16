package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.response.ListPhotoResponse;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;

import org.robobinding.annotation.PresentationModel;

import java.util.ArrayList;
import java.util.List;

@PresentationModel
public class TripImagesListFragmentPresentation extends BasePresentation<TripImagesListFragmentPresentation.View> implements DataManager.Result<ListPhotoResponse> {
    private TripImagesListFragment.Type type;
    private ArrayList<Photo> data;

    public TripImagesListFragmentPresentation(View view, TripImagesListFragment.Type type) {
        super(view);
        this.type = type;
    }

    public void loadImages() {
        switch (type) {
            case MY_IMAGES:
                dataManager.getMyPhotos(this);
                break;
            case MEMBER_IMAGES:
                dataManager.getMemberPhotos(this);
                break;
            case YOU_SHOULD_BE_HERE:
                dataManager.getYouShouldBeHerePhotos(this);

                break;
        }
    }

    @Override
    public void response(ListPhotoResponse listPhotoResponse, Exception e) {
        if (listPhotoResponse != null) {
            view.clearAdapter();
            data = listPhotoResponse.getData();
            view.setPhotos(data);
        } else if (e != null) {
            view.setPhotos(null);

            handleError(e);
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

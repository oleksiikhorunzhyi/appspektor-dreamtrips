package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.response.ListPhotoResponse;
import com.worldventures.dreamtrips.view.activity.Injector;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;

import org.robobinding.annotation.PresentationModel;

import java.util.ArrayList;
import java.util.List;

@PresentationModel
public class TripImagesListFragmentPresentation extends BasePresentation implements DataManager.Result<ListPhotoResponse> {
    private View view;
    private TripImagesListFragment.Type type;
    private ArrayList<Photo> data;

    public TripImagesListFragmentPresentation(View view, TripImagesListFragment.Type type, Injector injector) {
        super(view, injector);
        this.view = view;
        this.type = type;
    }

    public void loadImages() {
        switch (type) {
            case MY_IMAGES:
                dataManager.getMyPhotos(sessionManager, this);
                break;
            case MEMBER_IMAGES:
                dataManager.getMemberPhotos(sessionManager, this);
                break;
            case YOU_SHOULD_BE_HERE:
                dataManager.getYouShouldBeHerePhotos(sessionManager, this);

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
            handleError(e);
        }
    }

    public void onItemClick(int position) {
        activityRouter.openFullScreenPhoto(data, position);
    }

    public static interface View extends IInformView {
        void setPhotos(List<Photo> photos);

        void clearAdapter();
    }
}

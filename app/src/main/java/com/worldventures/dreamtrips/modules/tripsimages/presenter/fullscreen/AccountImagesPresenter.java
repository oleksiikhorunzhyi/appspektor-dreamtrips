package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetUserPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

public class AccountImagesPresenter extends MembersImagesPresenter {

    public AccountImagesPresenter(TripImagesType type, int userId) {
        super(type, userId);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return new TripImagesRoboSpiceController() {
            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
                GetUserPhotosQuery getMembersPhotosQuery = new GetUserPhotosQuery(photoUploadingManager, userId, PER_PAGE, 1);
                return getMembersPhotosQuery;
            }

            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
                GetUserPhotosQuery getMembersPhotosQuery = new GetUserPhotosQuery(photoUploadingManager, userId, PER_PAGE, currentCount / PER_PAGE + 1);
                return getMembersPhotosQuery;
            }
        };
    }

    @Override
    protected void photoUploaded(UploadTask task) {
        super.photoUploaded(task);
        doRequest(new AddTripPhotoCommand(task), photo -> {
            processPhoto(photos.indexOf(task), photo);
            uploadTags(photo.getFSId());
        }, spiceException -> {
            photoError(getCurrentTask(task.getId()));
        });
    }
}

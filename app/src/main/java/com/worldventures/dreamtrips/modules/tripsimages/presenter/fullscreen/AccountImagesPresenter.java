package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetUserPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.events.ImageUploadedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

public class AccountImagesPresenter extends MembersImagesPresenter {

    public AccountImagesPresenter(TripImagesType type, int userId) {
        super(type, userId);
    }

    @Override
    public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
        return new GetUserPhotosQuery(photoUploadingManager, userId, PER_PAGE, 1);
    }

    @Override
    public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
        return new GetUserPhotosQuery(photoUploadingManager, userId, PER_PAGE, currentCount / PER_PAGE + 1);
    }

    @Override
    public void onEventMainThread(ImageUploadedEvent event) {
        super.onEventMainThread(event);
        if (!fullscreenMode && event.isSuccess) {
            uploadTags(event.photo.getFSId());
        }
    }

    @Override
    protected void photoUploaded(UploadTask task) {
        super.photoUploaded(task);
        doRequest(new AddTripPhotoCommand(task),
                photo -> eventBus.post(new ImageUploadedEvent(true, task, photo)),
                spiceException -> eventBus.post(new ImageUploadedEvent(false, task, null)));
    }
}

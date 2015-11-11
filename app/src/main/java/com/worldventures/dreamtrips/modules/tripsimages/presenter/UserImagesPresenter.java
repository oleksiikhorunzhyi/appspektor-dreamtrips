package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.support.v4.app.Fragment;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetUserPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class UserImagesPresenter extends TripImagesListPresenter {

    public static final int REQUESTER_ID = -10;

    public UserImagesPresenter(int userId) {
        this(Type.MEMBER_IMAGES, userId);
    }

    public UserImagesPresenter(Type type, int userId) {
        super(type, userId);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return new TripImagesRoboSpiceController() {
            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
                return new GetUserPhotosQuery(PER_PAGE, 1);
            }

            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
                return new GetUserPhotosQuery(PER_PAGE, currentCount / PER_PAGE + 1);
            }
        };
    }

    public void pickImage(int requestType) {
        eventBus.post(new ImagePickRequestEvent(requestType, REQUESTER_ID));
    }

    public void onEvent(ImagePickedEvent event) {
        if (event.getRequesterID() == REQUESTER_ID) {
            eventBus.cancelEventDelivery(event);
            eventBus.removeStickyEvent(event);
            String fileThumbnail = event.getImages()[0].getFileThumbnail();
            imageSelected(Uri.parse(fileThumbnail), event.getRequestType());
        }
    }

    public void imageSelected(Uri uri, int requestType) {
        if (activityRouter != null) {
            String type = "";
            switch (requestType) {
                case PickImageDelegate.REQUEST_CAPTURE_PICTURE:
                    type = "camera";
                    break;
                case PickImageDelegate.REQUEST_PICK_PICTURE:
                    type = "album";
                    break;
                case PickImageDelegate.REQUEST_FACEBOOK:
                    type = "facebook";
                    break;
            }

            activityRouter.openCreatePhoto((Fragment) view, uri, type);
        }
    }

}

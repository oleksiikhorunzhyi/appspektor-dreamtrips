package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.support.v4.app.Fragment;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMyPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class MyImagesPresenter extends TripImagesListPresenter<MyImagesPresenter.View> {

    public static final int REQUESTER_ID = -1;

    public MyImagesPresenter() {
        super(Type.MY_IMAGES);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        User user = appSessionHolder.get().get().getUser();

        return new TripImagesRoboSpiceController() {
            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
                GetMyPhotosQuery getMyPhotosQuery = new GetMyPhotosQuery(user.getId(), PER_PAGE, 1);
                view.inject(getMyPhotosQuery);
                return getMyPhotosQuery;
            }

            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
                GetMyPhotosQuery getMyPhotosQuery = new GetMyPhotosQuery(user.getId(), PER_PAGE, currentCount / PER_PAGE + 1);
                view.inject(getMyPhotosQuery);
                return getMyPhotosQuery;
            }
        };
    }


    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setFabVisibility(true);
    }

    public void pickImage(int requestType) {
        eventBus.post(new ImagePickRequestEvent(requestType, REQUESTER_ID));
    }
    public interface View extends TripImagesListPresenter.View {
        void setFabVisibility(boolean visible);
    }

    public void onEvent(ImagePickedEvent event) {
        if (event.getRequesterID() == REQUESTER_ID) {
            eventBus.removeStickyEvent(event);
            imageSelected(Uri.parse(event.getImages()[0].getFilePathOriginal()), event.getRequestType());
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
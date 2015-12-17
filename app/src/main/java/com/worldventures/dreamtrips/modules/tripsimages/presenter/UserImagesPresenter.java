package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.support.v4.app.Fragment;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetUserPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.events.MyImagesSelectionEvent;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.ArrayList;
import java.util.List;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class UserImagesPresenter extends TripImagesListPresenter<UserImagesPresenter.View> {

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

    public void onEvent(AttachPhotoEvent event) {
        if (view.isVisibleOnScreen() && event.getRequestType() != -1)
            pickImage(event.getRequestType());
    }

    public void pickImage(int requestType) {
        if (view.isVisibleOnScreen())
            eventBus.post(new ImagePickRequestEvent(requestType, REQUESTER_ID));
    }

    public void onEvent(ImagePickedEvent event) {
        if (view.isVisibleOnScreen() && event.getRequesterID() == REQUESTER_ID) {
            eventBus.cancelEventDelivery(event);
            eventBus.removeStickyEvent(event);

            attachImages(Queryable.from(event.getImages()).toList(), event.getRequestType());
        }
    }

    public void attachImages(List<ChosenImage> photos, int type) {
        if (photos.size() == 0) {
            return;
        }

        eventBus.post(new MyImagesSelectionEvent());

        view.hidePhotoPicker();

        String fileThumbnail = photos.get(0).getFileThumbnail();
        imageSelected(Uri.parse(fileThumbnail), type);
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

    public interface View extends TripImagesListPresenter.View {
        void hidePhotoPicker();
    }
}

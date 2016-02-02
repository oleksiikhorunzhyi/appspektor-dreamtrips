package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import android.net.Uri;
import android.support.v4.app.Fragment;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMemberPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.events.MyImagesSelectionEvent;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * ALL MEMBERS PHOTOS. 1 TAB in Trip Images page.
 */
public class MembersImagesPresenter extends TripImagesListPresenter<MembersImagesPresenter.View> {

    public static final int REQUESTER_ID = -10;

    public MembersImagesPresenter() {
        this(TripImagesType.MEMBERS_IMAGES, 0);
    }

    public MembersImagesPresenter(TripImagesType type, int userId) {
        super(type, userId);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return new TripImagesRoboSpiceController() {
            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
                return new GetMemberPhotosQuery(PER_PAGE, 1);
            }

            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
                return new GetMemberPhotosQuery(PER_PAGE, currentCount / PER_PAGE + 1);
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
                case PickImageDelegate.CAPTURE_PICTURE:
                    type = "camera";
                    break;
                case PickImageDelegate.PICK_PICTURE:
                    type = "album";
                    break;
                case PickImageDelegate.FACEBOOK:
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
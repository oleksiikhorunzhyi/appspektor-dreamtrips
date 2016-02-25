package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMemberPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;

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
    protected SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
        return new GetMemberPhotosQuery(PER_PAGE, currentCount / PER_PAGE + 1);
    }

    @Override
    protected SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
        return new GetMemberPhotosQuery(PER_PAGE, 1);
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

            view.attachImages(Queryable.from(event.getImages()).toList(), event.getRequestType());
        }
    }

    public interface View extends TripImagesListPresenter.View {

        void attachImages(List<ChosenImage> photos, int requestType);
    }
}

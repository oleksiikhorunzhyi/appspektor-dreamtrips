package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.feed.event.PickerDoneEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMemberPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;

import java.util.ArrayList;

/**
 * ALL MEMBERS PHOTOS. 1 TAB in Trip Images page.
 */
public class MembersImagesPresenter extends TripImagesListPresenter<MembersImagesPresenter.View> {

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

    public void onEvent(PickerDoneEvent event) {
        view.openCreatePhoto(event.getMediaAttachment());
    }

    public interface View extends TripImagesListPresenter.View {

        void openCreatePhoto(MediaAttachment mediaAttachment);
    }
}

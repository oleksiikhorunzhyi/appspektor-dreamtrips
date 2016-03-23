package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMemberPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * ALL MEMBERS PHOTOS. 1 TAB in Trip Images page.
 */
public class MembersImagesPresenter extends TripImagesListPresenter<MembersImagesPresenter.View> {

    @Inject
    MediaPickerManager mediaPickerManager;

    public MembersImagesPresenter() {
        this(TripImagesType.MEMBERS_IMAGES, 0);
    }

    public MembersImagesPresenter(TripImagesType type, int userId) {
        super(type, userId);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.bind(mediaPickerManager.toObservable())
                .filter(attachment -> attachment.requestId == getMediaRequestId() && attachment.chosenImages.size() > 0)
                .subscribe(mediaAttachment -> {
                    view.attachImages(mediaAttachment.chosenImages, mediaAttachment.type);
                });
    }

    public int getMediaRequestId() {
        return MembersImagesPresenter.class.getSimpleName().hashCode();
    }

    @Override
    protected SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
        return new GetMemberPhotosQuery(PER_PAGE, currentCount / PER_PAGE + 1);
    }

    @Override
    protected SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
        return new GetMemberPhotosQuery(PER_PAGE, 1);
    }

    public interface View extends TripImagesListPresenter.View {

        void attachImages(List<ChosenImage> photos, int requestType);
    }
}

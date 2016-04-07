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

import rx.Subscription;

/**
 * ALL MEMBERS PHOTOS. 1 TAB in Trip Images page.
 */
public class MembersImagesPresenter extends TripImagesListPresenter<MembersImagesPresenter.View> {

    @Inject
    MediaPickerManager mediaPickerManager;

    private Subscription mediaSubscription;

    public MembersImagesPresenter() {
        this(TripImagesType.MEMBERS_IMAGES, 0);
    }

    public MembersImagesPresenter(TripImagesType type, int userId) {
        super(type, userId);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        mediaSubscription = mediaPickerManager.toObservable()
                .filter(attachment -> attachment.requestId == getMediaRequestId() && attachment.chosenImages.size() > 0)
                .subscribe(mediaAttachment -> {
                    view.attachImages(mediaAttachment.chosenImages, mediaAttachment.type);
                });
    }

    @Override
    public void dropView() {
        super.dropView();
        if (!mediaSubscription.isUnsubscribed()) mediaSubscription.unsubscribe();
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

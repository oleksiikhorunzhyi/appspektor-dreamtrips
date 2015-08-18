package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

public class TripImagesTabsPresenter extends Presenter<TripImagesTabsPresenter.View> {

    public static final int REQUESTER_ID = -1;

    public static final String SELECTION_EXTRA = "selection_extra";

    private int selection;

    public TripImagesTabsPresenter(Bundle args) {
        if (args != null) {
            selection = args.getInt(SELECTION_EXTRA);
        }
    }

    public void trackState(int position) {
        if (position == TripImagesListFragment.Type.MY_IMAGES.ordinal()) {
            TrackingHelper.mine(getAccountUserId());
        } else if (position == TripImagesListFragment.Type.YOU_SHOULD_BE_HERE.ordinal()) {
            TrackingHelper.ysbh(getAccountUserId());
        } else if (position == TripImagesListFragment.Type.MEMBER_IMAGES.ordinal()) {
            TrackingHelper.all(getAccountUserId());
        } else if (position == TripImagesListFragment.Type.VIDEO_360.ordinal()) {
            TrackingHelper.video360(getAccountUserId());
        } else if (position == TripImagesListFragment.Type.INSPIRE_ME.ordinal()) {
            TrackingHelper.inspr(getAccountUserId());
        }
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setFabVisibility(true);
        view.setSelection(selection);
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

    public void pickImage(int requestType) {
        eventBus.post(new ImagePickRequestEvent(requestType, REQUESTER_ID));
    }

    public void onEvent(ImagePickedEvent event) {
        if (event.getRequesterID() == REQUESTER_ID) {
            eventBus.removeStickyEvent(event);
            imageSelected(Uri.parse(event.getImages()[0].getFilePathOriginal()), event.getRequestType());
        }
    }

    @Override
    public void dropView() {
        eventBus.removeAllStickyEvents();
        super.dropView();
    }

    public interface View extends Presenter.View {
        void setFabVisibility(boolean visibility);

        void setSelection(int selection);
    }
}

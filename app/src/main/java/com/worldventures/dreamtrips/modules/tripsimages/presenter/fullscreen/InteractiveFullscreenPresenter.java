package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import android.content.Context;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoLikeEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.FlagPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.LikePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.UnlikePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.INSPIRE_ME;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.MEMBER_IMAGES;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.YOU_SHOULD_BE_HERE;

public class InteractiveFullscreenPresenter extends FullScreenPresenter<Photo> {

    @Inject
    protected Context context;

    public void onDeleteAction() {
        doRequest(new DeletePhotoCommand(photo.getFsId()), (jsonObject) -> {
            view.informUser(context.getString(R.string.photo_deleted));
            eventBus.postSticky(new PhotoDeletedEvent(photo.getFsId()));
        });
    }

    public void sendFlagAction(String title, String description) {
        String desc = "";
        if (description != null) {
            desc = description;
        }
        doRequest(new FlagPhotoCommand(photo.getFsId(), title + ". " + desc), obj -> {
            view.informUser(context.getString(R.string.photo_flagged));
            TrackingHelper.flag(type, String.valueOf(photo.getFsId()), getAccountUserId());
        });
    }

    public void onLikeAction() {
        DreamTripsRequest dreamTripsRequest = !photo.isLiked() ?
                new LikePhotoCommand(photo.getFsId()) :
                new UnlikePhotoCommand(photo.getFsId());
        doRequest(dreamTripsRequest, obj -> onLikeSuccess(), exc -> onLikeFailure());
    }

    private void onLikeFailure() {
        view.informUser(context.getString(R.string.can_not_like_photo));
    }

    private void onLikeSuccess() {
        boolean isLiked = !photo.isLiked();
        photo.setLiked(isLiked);
        int likesCount = photo.getLikesCount();
        int actualLikeCount = isLiked ? likesCount + 1 : likesCount - 1;
        photo.setLikesCount(actualLikeCount);
        view.setLiked(isLiked);
        view.setLikeCount(actualLikeCount);
        eventBus.postSticky(new PhotoLikeEvent(photo.getFsId(), isLiked));
        TrackingHelper.like(type, String.valueOf(photo.getFsId()), getAccountUserId());
    }

    @Override
    protected boolean isLiked() {
        return photo.isLiked();
    }


    protected boolean isFlagVisible() {
        return type == MEMBER_IMAGES && getAccount().getId() != photo.getUser().getId();
    }

    protected boolean isDeleteVisible() {
        return photo.getUser() != null && getAccount().getId() == photo.getUser().getId();
    }

    protected boolean isLikeVisible() {
        return type != YOU_SHOULD_BE_HERE && type != INSPIRE_ME;
    }
}

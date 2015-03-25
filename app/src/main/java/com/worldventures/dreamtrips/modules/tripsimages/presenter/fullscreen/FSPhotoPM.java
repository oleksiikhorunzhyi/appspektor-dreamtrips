package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoLikeEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.FlagPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.LikePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.UnlikePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.INSPIRE_ME;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.MEMBER_IMAGES;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.YOU_SHOULD_BE_HERE;

public class FSPhotoPM extends FSViewPM<Photo> {
    @Inject
    Context context;

    public FSPhotoPM(View view) {
        super(view);
    }

    public void onDeleteAction() {
        dreamSpiceManager.execute(new DeletePhotoCommand(photo.getId()), new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                view.informUser(context.getString(R.string.photo_deleted));
                eventBus.postSticky(new PhotoDeletedEvent(photo.getId()));

            }
        });
    }

    public void sendFlagAction(String title, String desc) {
        if (desc == null) desc = "";
        dreamSpiceManager.execute(new FlagPhotoCommand(photo.getId(), title + ". " + desc), new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(this.getClass().getSimpleName(), "", spiceException);
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                view.informUser(context.getString(R.string.photo_flagged));
                AdobeTrackingHelper.flag(type, String.valueOf(photo.getId()), getUserId());

            }
        });
    }

    public void onLikeAction() {
        final RequestListener<JsonObject> callback = new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                view.informUser("Can't like photo");
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                boolean isLiked = !photo.isLiked();
                photo.setLiked(isLiked);
                int likesCount = photo.getLikesCount();
                int actualLikeCount = isLiked ? likesCount + 1 : likesCount - 1;
                photo.setLikesCount(actualLikeCount);
                view.setLiked(isLiked);
                view.setLikeCount(actualLikeCount);
                eventBus.postSticky(new PhotoLikeEvent(photo.getId(), isLiked));
                AdobeTrackingHelper.like(type, String.valueOf(photo.getId()), getUserId());
            }
        };

        if (!photo.isLiked()) {
            dreamSpiceManager.execute(new LikePhotoCommand(photo.getId()), callback);
        } else {
            dreamSpiceManager.execute(new UnlikePhotoCommand(photo.getId()), callback);
        }
    }

    @Override
    protected boolean isLiked() {
        return photo.isLiked();
    }


    protected boolean isFlagVisible() {
        return type == MEMBER_IMAGES && user.getId() != photo.getUser().getId();
    }

    protected boolean isDeleteVisible() {
        return photo.getUser() != null && user.getId() == photo.getUser().getId();
    }

    protected boolean isLikeVisible() {
        return type != YOU_SHOULD_BE_HERE && type != INSPIRE_ME;
    }
}

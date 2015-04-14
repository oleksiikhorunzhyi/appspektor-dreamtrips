package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.R;
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

public class FSPhotoPresenter extends FullScreenPresenter<Photo> {
    @Inject
    protected Context context;

    public FSPhotoPresenter(View view) {
        super(view);
    }

    public void onDeleteAction() {
        dreamSpiceManager.execute(new DeletePhotoCommand(photo.getFsId()), new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.v(this.getClass().getSimpleName(), "onRequestFailure");
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                view.informUser(context.getString(R.string.photo_deleted));
                eventBus.postSticky(new PhotoDeletedEvent(photo.getFsId()));

            }
        });
    }

    public void sendFlagAction(String title, String description) {
        String desc = "";
        if (description != null) {
            desc = description;
        }
        dreamSpiceManager.execute(new FlagPhotoCommand(photo.getFsId(), title + ". " + desc), new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(this.getClass().getSimpleName(), "", spiceException);
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                view.informUser(context.getString(R.string.photo_flagged));
                TrackingHelper.flag(type, String.valueOf(photo.getFsId()), getUserId());

            }
        });
    }

    public void onLikeAction() {
        final RequestListener<JsonObject> callback = new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                view.informUser(context.getString(R.string.can_not_like_photo));
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
                eventBus.postSticky(new PhotoLikeEvent(photo.getFsId(), isLiked));
                TrackingHelper.like(type, String.valueOf(photo.getFsId()), getUserId());
            }
        };

        if (!photo.isLiked()) {
            dreamSpiceManager.execute(new LikePhotoCommand(photo.getFsId()), callback);
        } else {
            dreamSpiceManager.execute(new UnlikePhotoCommand(photo.getFsId()), callback);
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

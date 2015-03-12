package com.worldventures.dreamtrips.presentation.fullscreen;

import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.utils.anotation.IgnoreRobobinding;
import com.worldventures.dreamtrips.utils.busevents.PhotoDeletedEvent;
import com.worldventures.dreamtrips.utils.busevents.PhotoLikeEvent;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type.INSPIRE_ME;
import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type.MEMBER_IMAGES;
import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type.YOU_SHOULD_BE_HERE;

@IgnoreRobobinding
public class FSPhotoPM extends BaseFSViewPM<Photo> {
    public FSPhotoPM(View view) {
        super(view);
    }


    public void onDeleteAction() {
        dreamSpiceManager.execute(new DreamTripsRequest.DeletePhoto(photo.getId()), new RequestListener<JsonObject>() {
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
        dreamSpiceManager.execute(new DreamTripsRequest.FlagPhoto(photo.getId(), title + ". " + desc), new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                handleError(spiceException);

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

            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                boolean isLiked = photo.isLiked();
                photo.setLiked(!isLiked);
                view.setLiked(!isLiked);
                eventBus.postSticky(new PhotoLikeEvent(photo.getId(), !isLiked));
                AdobeTrackingHelper.like(type, String.valueOf(photo.getId()),getUserId());
            }
        };

        if (!photo.isLiked()) {
            dreamSpiceManager.execute(new DreamTripsRequest.LikePhoto(photo.getId()), callback);
        } else {
            dreamSpiceManager.execute(new DreamTripsRequest.UnlikePhoto(photo.getId()), callback);
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

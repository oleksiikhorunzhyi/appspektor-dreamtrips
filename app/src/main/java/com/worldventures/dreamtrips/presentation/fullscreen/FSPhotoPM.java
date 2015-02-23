package com.worldventures.dreamtrips.presentation.fullscreen;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.utils.anotation.IgnoreRobobinding;
import com.worldventures.dreamtrips.utils.busevents.PhotoDeletedEvent;
import com.worldventures.dreamtrips.utils.busevents.PhotoLikeEvent;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type.INSPIRE_ME;
import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type.MEMBER_IMAGES;
import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type.YOU_SHOULD_BE_HERE;

@IgnoreRobobinding
public class FSPhotoPM extends BaseFSViewPM<Photo> {
    public FSPhotoPM(View view) {
        super(view);
    }


    public void onDeleteAction() {
        dreamTripsApi.deletePhoto(photo.getId(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                view.informUser(context.getString(R.string.photo_deleted));
                eventBus.postSticky(new PhotoDeletedEvent(photo.getId()));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void sendFlagAction(String title, String desc) {
        if (desc == null) desc = "";
        dreamTripsApi.flagPhoto(photo.getId(), title + ". " + desc, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                view.informUser(context.getString(R.string.photo_flagged));
                AdobeTrackingHelper.flag(type, String.valueOf(photo.getId()));
            }

            @Override
            public void failure(RetrofitError error) {
                handleError(error);
            }
        });
    }

    public void onLikeAction() {
        final Callback<JsonObject> callback = new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean isLiked = photo.isLiked();
                photo.setLiked(!isLiked);
                view.setLiked(!isLiked);
                eventBus.postSticky(new PhotoLikeEvent(photo.getId(), !isLiked));
                AdobeTrackingHelper.like(type, String.valueOf(photo.getId()));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        };

        if (!photo.isLiked()) {
            dreamTripsApi.likePhoto(photo.getId(), callback);
        } else {
            dreamTripsApi.unlikePhoto(photo.getId(), callback);
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

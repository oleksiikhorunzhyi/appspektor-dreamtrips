package com.worldventures.dreamtrips.presentation;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.FlagContent;
import com.worldventures.dreamtrips.core.model.Photo;

import org.robobinding.annotation.PresentationModel;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@PresentationModel
public class FullScreenPhotoFragmentPM extends BasePresentation<FullScreenPhotoFragmentPM.View> {

    @Inject
    DreamTripsApi dreamTripsApi;

    Photo photo;

    public FullScreenPhotoFragmentPM(View view) {
        super(view);
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void onLikeAction() {
        final Callback<JsonObject> callback = new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean isLiked = photo.isLiked();
                photo.setLiked(!isLiked);
                view.setLiked(!isLiked);
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

    public void onCreate() {
        view.setTitle(photo.getTitle());
        view.setLiked(photo.isLiked());
    }

    public void showFlagAction(int itemId) {
        FlagContent flagContent = FlagContent.values()[itemId];
        if (flagContent.isNeedDescription()) {
            view.showFlagDescription(flagContent.getTitle());
        } else {
            view.showFlagConfirmDialog(flagContent.getTitle(), null);
        }
    }

    public void sendFlagAction(String title, String desc) {
        if (desc == null) desc = "";
        dreamTripsApi.flagPhoto(photo.getId(), title + ". " + desc, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                view.informUser("Photo has been flagged");
            }

            @Override
            public void failure(RetrofitError error) {
                handleError(error);
            }
        });
    }

    public static interface View extends BasePresentation.View {
        void setTitle(String title);

        void setLiked(boolean isLiked);

        public void showFlagConfirmDialog(String reason, String desc);

        public void showFlagDescription(String reason);
    }
}

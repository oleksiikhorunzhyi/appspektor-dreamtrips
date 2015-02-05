package com.worldventures.dreamtrips.presentation;

import com.google.gson.JsonObject;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.FlagContent;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.utils.busevents.PhotoDeletedEvent;
import com.worldventures.dreamtrips.utils.busevents.PhotoLikeEvent;

import org.robobinding.annotation.PresentationModel;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;
import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type.INSPIRE_ME;
import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type.MEMBER_IMAGES;
import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type.YOU_SHOULD_BE_HERE;

@PresentationModel
public class FullScreenPhotoFragmentPM extends BasePresentation<FullScreenPhotoFragmentPM.View> {

    @Inject
    DreamTripsApi dreamTripsApi;
    @Inject
    @Global
    EventBus eventBus;

    Photo photo;
    private Type type;
    private User user;

    public FullScreenPhotoFragmentPM(View view) {
        super(view);

    }

    public void setupPhoto(Photo photo) {
        this.photo = photo;
    }

    public void setupType(Type type) {
        this.type = type;
    }

    public Photo providePhoto() {
        return photo;
    }

    public void onLikeAction() {
        final Callback<JsonObject> callback = new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean isLiked = photo.isLiked();
                photo.setLiked(!isLiked);
                view.setLiked(!isLiked);
                eventBus.postSticky(new PhotoLikeEvent(photo.getId(), !isLiked));
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
        user = appSessionHolder.get().get().getUser();
    }

    public void setupActualViewState() {
        view.setTitle(photo.getTitle());
        view.setLiked(photo.isLiked());
        view.setLikeVisibility(type != YOU_SHOULD_BE_HERE && type != INSPIRE_ME);
        view.setDeleteVisibility(photo.getUser() != null && user.getId() == photo.getUser().getId());
        view.setFlagVisibility(type == MEMBER_IMAGES && user.getId() != photo.getUser().getId());
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

    public void delete() {
        dreamTripsApi.deletePhoto(photo.getId(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                view.informUser("Photo has been deleted");
                eventBus.postSticky(new PhotoDeletedEvent(photo.getId()));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public static interface View extends BasePresentation.View {
        void setTitle(String title);

        void setLiked(boolean isLiked);

        void setFlagVisibility(boolean isVisible);

        void setDeleteVisibility(boolean isVisible);

        void setLikeVisibility(boolean isVisible);

        public void showFlagConfirmDialog(String reason, String desc);

        public void showFlagDescription(String reason);
    }
}

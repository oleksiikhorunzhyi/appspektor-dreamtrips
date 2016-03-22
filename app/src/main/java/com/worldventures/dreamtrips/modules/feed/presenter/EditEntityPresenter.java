package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.feed.api.EditPostCommand;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder.Type;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.api.EditTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoTagsBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

public class EditEntityPresenter extends ActionEntityPresenter<ActionEntityPresenter.View> {

    @State
    FeedEntity entity;

    @State
    Location cachedLocation;

    private Type type;

    @NotNull
    private final String IMMUTABLE_DESCRIPTION;
    @NotNull
    private final Location IMMUTABLE_LOCATION;

    public EditEntityPresenter(FeedEntity entity, Type type) {
        this.entity = entity;
        this.type = type;
        String description;
        switch (type) {
            case PHOTO:
                description = ((Photo) entity).getTitle();
                break;
            case POST:
                description = ((TextualPost) entity).getDescription();
                break;
            default:
                description = "";
                break;
        }
        cachedText = IMMUTABLE_DESCRIPTION = description != null ? description : "";

        IMMUTABLE_LOCATION = entity instanceof Photo ? ((Photo) entity).getLocation() : new Location();
        cachedLocation = new Location(IMMUTABLE_LOCATION);

    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (type == Type.PHOTO) {
            view.attachPhoto(Uri.parse(((Photo) entity).getFSImage().getUrl()));
        }
        invalidateDynamicViews();
    }

    @Override
    protected EditPhotoTagsBundle.PhotoEntity getImageForTagging() {
        return new EditPhotoTagsBundle.PhotoEntity(((Photo) entity).getImages().getUrl(), null);
    }

    @Override
    protected boolean isChanged() {
        return !IMMUTABLE_DESCRIPTION.equals(cachedText)
                || !cachedAddedPhotoTags.isEmpty() || !cachedRemovedPhotoTags.isEmpty()
                || !IMMUTABLE_LOCATION.equals(cachedLocation);
    }

    public void invalidateAddTagBtn() {
        boolean isViewShown = type == Type.PHOTO;
        boolean someTagSets = !((Photo) entity).getPhotoTags().isEmpty() || !cachedAddedPhotoTags.isEmpty();
        if (view != null) {
            view.redrawTagButton(isViewShown, someTagSets);
        }
    }

    @Override
    protected List<PhotoTag> getCombinedTags() {
        ArrayList<PhotoTag> originPhotoTags = new ArrayList<>(((Photo) entity).getPhotoTags());
        originPhotoTags.removeAll(cachedRemovedPhotoTags);

        List<PhotoTag> combinedTags = super.getCombinedTags();
        combinedTags.addAll(originPhotoTags);
        return combinedTags;
    }

    @Override
    public Location getLocation() {
        return cachedLocation;
    }

    @Override
    protected void invalidateDynamicViews() {
        super.invalidateDynamicViews();
        invalidatePostBtn();
    }

    private void invalidatePostBtn() {
        if (isChanged()) {
            view.enableButton();
        } else {
            view.disableButton();
        }
    }

    @Override
    public void post() {
        switch (type) {
            case PHOTO:
                updatePhoto();
                break;
            case POST:
                updatePost();
                break;
        }
    }

    private void updatePost() {
        doRequest(new EditPostCommand(entity.getUid(), cachedText),
                this::processPostSuccess, spiceException -> {
                    handleError(spiceException);
                    view.onPostError();
                });
    }

    private void updatePhoto() {
        UploadTask uploadTask = new UploadTask();
        uploadTask.setTitle(cachedText);
        uploadTask.setLocationName(cachedLocation.getName());
        uploadTask.setLatitude((float) cachedLocation.getLat());
        uploadTask.setLongitude((float) cachedLocation.getLng());
        doRequest(new EditTripPhotoCommand(entity.getUid(), uploadTask),
                this::processPostSuccess, spiceException -> {
                    handleError(spiceException);
                    view.onPostError();
                });
    }

    @Override
    public void updateLocation(Location location) {
        cachedLocation = location;
    }

    protected void processTagUploadSuccess(FeedEntity feedEntity) {
        eventBus.post(new FeedEntityChangedEvent(feedEntity));
        super.processTagUploadSuccess(feedEntity);
    }
}

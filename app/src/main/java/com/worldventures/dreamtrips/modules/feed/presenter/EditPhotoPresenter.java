package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.api.EditPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class EditPhotoPresenter extends ActionEntityPresenter<EditPhotoPresenter.View> {

    private Photo photo;

    public EditPhotoPresenter(Photo photo) {
        this.photo = photo;
    }

    @Override
    public void takeView(View view) {
        cachedCreationItems.add(createItemFromPhoto(photo));
        //
        super.takeView(view);
        //
        if (location == null) updateLocation(photo.getLocation());
    }

    @Override
    protected void updateUi() {
        view.setName(getAccount().getFullName());
        view.setAvatar(getAccount());
        view.attachPhotos(cachedCreationItems);
    }

    @Override
    protected boolean isChanged() {
        PhotoCreationItem item = cachedCreationItems.get(0);
        Location photoLocation = photo.getLocation();
        return (photoLocation.getLat() != location.getLat() || photoLocation.getLng() != location.getLng())
                || item.getCachedAddedPhotoTags().size() > 0 || item.getCachedRemovedPhotoTags().size() > 0
                || !photo.getTitle().equals(item.getTitle());
    }

    @Override
    public void post() {
        updatePhoto();
    }

    private void updatePhoto() {
        UploadTask uploadTask = new UploadTask();
        PhotoCreationItem creationItem = cachedCreationItems.get(0);
        uploadTask.setTitle(creationItem.getTitle());
        uploadTask.setLocationName(location.getName());
        uploadTask.setLatitude((float) location.getLat());
        uploadTask.setLongitude((float) location.getLng());
        uploadTask.setShotAt(photo.getShotAt());
        doRequest(new EditPhotoCommand(photo.getUid(), uploadTask,
                creationItem.getCachedAddedPhotoTags(), creationItem.getCachedRemovedPhotoTags()),
                entity -> {
                    eventBus.post(new FeedEntityChangedEvent(entity));
                    view.cancel();
                }, spiceException -> {
                    handleError(spiceException);
                    view.onPostError();
                });
    }

    public interface View extends ActionEntityPresenter.View {

    }
}

package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.tripsimages.api.EditPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class EditPhotoPresenter extends ActionEntityPresenter<EditPhotoPresenter.View> {

    private Photo photo;

    public EditPhotoPresenter(Photo photo) {
        this.photo = photo;
    }

    @Override
    public void takeView(View view) {
        if (cachedCreationItems.size() == 0)
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
        return isLocationChanged() || isTagsChanged() || isTitleChanged();
    }

    private boolean isLocationChanged() {
        return photo.getLocation().getLat() != location.getLat() || photo.getLocation().getLng() != location.getLng();
    }

    private boolean isTagsChanged() {
        PhotoCreationItem item = cachedCreationItems.get(0);
        return item.getCachedAddedPhotoTags().size() > 0 || item.getCachedRemovedPhotoTags().size() > 0;
    }

    private boolean isTitleChanged() {
        PhotoCreationItem item = cachedCreationItems.get(0);
        return !item.getTitle().equals(photo.getTitle());
    }

    @Override
    public void post() {
        updatePhoto();
    }

    @Override
    protected PhotoCreationItem createItemFromPhoto(Photo photo) {
        PhotoCreationItem item = super.createItemFromPhoto(photo);
        item.setCanDelete(false);
        return item;
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

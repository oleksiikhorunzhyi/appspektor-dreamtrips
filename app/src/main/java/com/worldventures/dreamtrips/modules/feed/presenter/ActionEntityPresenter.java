package com.worldventures.dreamtrips.modules.feed.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddPhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

public abstract class ActionEntityPresenter<V extends ActionEntityPresenter.View> extends Presenter<V> {

    @State
    String cachedText = "";
    @State
    ArrayList<PhotoTag> cachedAddedPhotoTags = new ArrayList<>();
    @State
    ArrayList<PhotoTag> cachedRemovedPhotoTags = new ArrayList<>();

    @Override
    public void takeView(V view) {
        super.takeView(view);
        updateUi();
    }

    protected void updateUi() {
        view.setName(getAccount().getFullName());
        view.setAvatar(getAccount());
        view.setText(cachedText);
    }

    public void cancelClicked() {
        if (view != null) {
            if (isChanged()) {
                view.showCancelationDialog();
            } else {
                view.cancel();
            }
        }
    }

    protected abstract boolean isChanged();

    public void postInputChanged(String input) {
        cachedText = input;
        invalidateDynamicViews();
    }

    public abstract void updateLocation(Location location);

    protected void invalidateDynamicViews() {
    }

    public abstract void post();

    protected List<PhotoTag> getCombinedTags() {
        return new ArrayList<>(cachedAddedPhotoTags);
    }

    public void onTagSelected(ArrayList<PhotoTag> photoTags, ArrayList<PhotoTag> removedTags) {
        cachedAddedPhotoTags.removeAll(photoTags);
        cachedAddedPhotoTags.addAll(photoTags);
        cachedAddedPhotoTags.removeAll(removedTags);

        cachedRemovedPhotoTags.removeAll(removedTags);
        cachedRemovedPhotoTags.addAll(removedTags);
    }

    public abstract Location getLocation();

    protected void pushTags(FeedEntity feedEntity) {
        cachedAddedPhotoTags.removeAll(((Photo) feedEntity).getPhotoTags());
        if (cachedAddedPhotoTags.size() > 0) {
            doRequest(new AddPhotoTagsCommand(feedEntity.getUid(), cachedAddedPhotoTags), aVoid -> {
                if (cachedRemovedPhotoTags.size() > 0) {
                    postRemovedPhotoTags(feedEntity);
                } else {
                    processTagUploadSuccess(feedEntity);
                }
            });
        } else if (cachedRemovedPhotoTags.size() > 0) {
            postRemovedPhotoTags(feedEntity);
        } else {
            processTagUploadSuccess(feedEntity);
        }
    }

    private void postRemovedPhotoTags(FeedEntity feedEntity) {
        List<Integer> userIds = Queryable.from(cachedRemovedPhotoTags)
                .concat(((Photo) feedEntity).getPhotoTags()).map(photo -> photo.getUser().getId()).toList();
        doRequest(new DeletePhotoTagsCommand(feedEntity.getUid(), userIds), bVoid -> {
            processTagUploadSuccess(feedEntity);
        });
    }

    protected void processPostSuccess(FeedEntity feedEntity) {
        closeView();
    }

    protected void processPhotoSuccess(FeedEntity feedEntity) {
        pushTags(feedEntity);
    }

    protected void processTagUploadSuccess(FeedEntity feedEntity) {
        Photo photo = (Photo) feedEntity;
        photo.getPhotoTags().addAll(cachedAddedPhotoTags);
        photo.getPhotoTags().removeAll(cachedRemovedPhotoTags);
        photo.setPhotoTagsCount(photo.getPhotoTags().size());

        closeView();
    }

    private void closeView() {
        view.cancel();
        view = null;
    }

    public void onLocationClicked() {
        view.openLocation(getLocation());
    }

    public interface View extends RxView {

        void attachPhotos(List<UploadTask> images);

        void setName(String userName);

        void setAvatar(User user);

        void setText(String text);

        void cancel();

        void showCancelationDialog();

        void enableButton();

        void disableButton();

        void onPostError();

        void openLocation(Location location);
    }

}

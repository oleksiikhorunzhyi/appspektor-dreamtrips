package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

public abstract class ActionEntityPresenter<V extends ActionEntityPresenter.View> extends Presenter<V> {

    @State
    String cachedText = "";

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

    public abstract void onTagSelected(long requestId, ArrayList<PhotoTag> photoTags, ArrayList<PhotoTag> removedTags);

    public abstract Location getLocation();

    protected void pushTags(FeedEntity feedEntity) {
    /* todo   cachedAddedPhotoTags.removeAll(((Photo) feedEntity).getPhotoTags());
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
        }*/
        processTagUploadSuccess(feedEntity);
    }

    private void postRemovedPhotoTags(FeedEntity feedEntity) {
     /*todo   List<Integer> userIds = Queryable.from(cachedRemovedPhotoTags)
                .concat(((Photo) feedEntity).getPhotoTags()).map(photo -> photo.getUser().getId()).toList();
        doRequest(new DeletePhotoTagsCommand(feedEntity.getUid(), userIds), bVoid -> {
            processTagUploadSuccess(feedEntity);
        });*/
    }

    protected void processPostSuccess(FeedEntity feedEntity) {
        closeView();
    }

    protected void processTagUploadSuccess(FeedEntity feedEntity) {
      /* TODO Photo photo = (Photo) feedEntity;
        photo.getPhotoTags().addAll(cachedAddedPhotoTags);
        photo.getPhotoTags().removeAll(cachedRemovedPhotoTags);
        photo.setPhotoTagsCount(photo.getPhotoTags().size());
      */
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

        void attachPhotos(List<PhotoCreationItem> images);

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

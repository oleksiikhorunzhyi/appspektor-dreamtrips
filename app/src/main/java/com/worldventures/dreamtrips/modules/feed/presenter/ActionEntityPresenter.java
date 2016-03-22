package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddPhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoTagsBundle;
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
        invalidateAddTagBtn();
    }

    protected void updateUi() {
        view.setName(getAccount().getFullName());
        view.setAvatar(getAccount().getAvatar().getThumb());
        view.setText(cachedText);
    }

    public void cancelClicked() {
        if (isChanged()) {
            view.showCancelationDialog();
        } else {
            view.cancel();
        }
    }

    protected abstract boolean isChanged();

    public abstract void invalidateAddTagBtn();

    public void postInputChanged(String input) {
        cachedText = input;
        invalidateDynamicViews();
    }

    public abstract void updateLocation(Location location);

    protected void invalidateDynamicViews() {
        invalidateAddTagBtn();
    }

    public abstract void post();

    public void onTagClicked() {
        view.showPhotoTagView(getImageForTagging(), getCombinedTags());
    }

    protected abstract EditPhotoTagsBundle.PhotoEntity getImageForTagging();

    protected List<PhotoTag> getCombinedTags() {
        return new ArrayList<>(cachedAddedPhotoTags);
    }

    public void onTagSelected(ArrayList<PhotoTag> photoTags, ArrayList<PhotoTag> removedTags) {
        cachedAddedPhotoTags = photoTags;
        cachedRemovedPhotoTags = removedTags;
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
        List<Integer> userIds = Queryable.from(cachedAddedPhotoTags)
                .concat(((Photo) feedEntity).getPhotoTags()).map(photo -> photo.getUser().getId()).toList();
        doRequest(new DeletePhotoTagsCommand(feedEntity.getUid(), userIds), bVoid -> {
            processTagUploadSuccess(feedEntity);
        });
    }

    protected void processPostSuccess(FeedEntity feedEntity) {
        pushTags(feedEntity);
    }

    protected void processTagUploadSuccess(FeedEntity feedEntity) {
        ((Photo) feedEntity).getPhotoTags().addAll(cachedAddedPhotoTags);
        ((Photo) feedEntity).getPhotoTags().removeAll(cachedRemovedPhotoTags);
        view.cancel();
        view = null;
    }

    public void onLocationClicked() {
        view.openLocation(getLocation());
    }

    public interface View extends RxView {

        void attachPhoto(Uri uri);

        void setName(String userName);

        void setAvatar(String avatarUrl);

        void setText(String text);

        void cancel();

        void showCancelationDialog();

        void enableButton();

        void disableButton();

        void onPostError();

        void redrawTagButton(boolean isViewShown, boolean someTagSets);

        void showPhotoTagView(EditPhotoTagsBundle.PhotoEntity photoEntity, List<PhotoTag> photoTags);

        void openLocation(Location location);
    }

}

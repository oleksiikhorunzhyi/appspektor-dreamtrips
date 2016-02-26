package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.feed.api.EditPostCommand;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder.Type;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.api.EditTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class EditEntityPresenter extends ActionEntityPresenter<ActionEntityPresenter.View> {

    private FeedEntity entity;
    private Type type;

    private final String IMMUTABLE_DESCRIPTION;

    public EditEntityPresenter(FeedEntity entity, Type type) {
        this.entity = entity;
        this.type = type;
        switch (type) {
            case PHOTO:
                IMMUTABLE_DESCRIPTION = ((Photo) entity).getTitle();
                break;
            case POST:
                IMMUTABLE_DESCRIPTION = ((TextualPost) entity).getDescription();
                break;
            default:
                IMMUTABLE_DESCRIPTION = "";
                break;
        }
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (type == Type.PHOTO) {
            view.attachPhoto(Uri.parse(((Photo) entity).getFSImage().getUrl()));
        }
    }

    @Override
    protected void updateUi() {
        cachedPostEntity.setText(IMMUTABLE_DESCRIPTION);
        super.updateUi();
    }

    @Override
    protected boolean isChanged() {
        return !cachedPostEntity.getText().equals(IMMUTABLE_DESCRIPTION);
    }

    @Override
    protected void enablePostButton() {
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
        doRequest(new EditPostCommand(entity.getUid(), cachedPostEntity.getText()),
                this::processPost, spiceException -> {
                    handleError(spiceException);
                    view.onPostError();
                });
    }

    private void updatePhoto() {
        UploadTask uploadTask = new UploadTask();
        uploadTask.setTitle(cachedPostEntity.getText());
        doRequest(new EditTripPhotoCommand(entity.getUid(), uploadTask),
                this::processPost, spiceException -> {
                    handleError(spiceException);
                    view.onPostError();
                });
    }

    protected void processPost(FeedEntity feedEntity) {
        eventBus.post(new FeedEntityChangedEvent(feedEntity));
        view.cancel();
        view = null;
    }
}

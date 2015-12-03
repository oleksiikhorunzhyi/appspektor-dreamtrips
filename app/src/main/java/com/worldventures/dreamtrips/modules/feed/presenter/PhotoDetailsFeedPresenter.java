package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class PhotoDetailsFeedPresenter extends Presenter<PhotoDetailsFeedPresenter.View> {

    private Photo photo;

    public PhotoDetailsFeedPresenter(Photo args) {
        this.photo = args;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setupView(photo);
    }

    public void onEvent(FeedEntityChangedEvent event) {
        if (event.getFeedEntity().getUid().equals(photo.getUid())) {
            photo = (Photo) event.getFeedEntity();
            view.setupView(photo);
        }
    }

    public void onDelete() {
        if (view.isVisibleOnScreen())
            doRequest(new DeletePhotoCommand(photo.getUid()),
                    aVoid -> itemDeleted());
    }

    private void itemDeleted() {
        eventBus.post(new FeedEntityDeletedEvent(photo));
        view.back();
    }

    public void onEdit() {
        if (view != null) view.moveToEdit(photo);
    }

    public interface View extends Presenter.View {

        void setupView(Photo photo);

        void moveToEdit(Photo photo);

        void back();
    }
}

package com.messenger.ui.presenter;

import com.messenger.entities.PhotoAttachment;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;

public class MessageImageFullscreenPresenter extends FullScreenPresenter<PhotoAttachment, MessageImageFullscreenPresenter.View> {

    public MessageImageFullscreenPresenter(PhotoAttachment photo, TripImagesType type) {
        super(photo, type);
    }

    public interface View extends FullScreenPresenter.View {
    }

}

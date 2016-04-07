package com.messenger.ui.presenter;

import com.messenger.entities.PhotoAttachment;
import com.messenger.util.ChatTimestampFormatter;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;

public class MessageImageFullscreenPresenter extends FullScreenPresenter<PhotoAttachment, MessageImageFullscreenPresenter.View> {

    private ChatTimestampFormatter timestampFormatter;

    public MessageImageFullscreenPresenter(PhotoAttachment photo, TripImagesType type) {
        super(photo, type);
    }

    @Override
    public void onInjected() {
        super.onInjected();
        timestampFormatter = new ChatTimestampFormatter(context.getApplicationContext());
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (photo.getDate() != null) {
            String dateLabel = timestampFormatter.getMessageDateDividerTimestamp(photo.getDate().getTime());
            view.setDateLabel(dateLabel);
        }
    }

    public interface View extends FullScreenPresenter.View {
        void setDateLabel(String dateLabel);
    }

}

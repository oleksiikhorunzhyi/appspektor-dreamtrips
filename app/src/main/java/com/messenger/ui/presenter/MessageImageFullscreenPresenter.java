package com.messenger.ui.presenter;

import com.messenger.entities.PhotoAttachment;
import com.messenger.ui.module.flagging.FlaggingPresenter;
import com.messenger.ui.module.flagging.FlaggingPresenterImpl;
import com.messenger.ui.module.flagging.FlaggingView;
import com.messenger.ui.util.chat.ChatTimestampFormatter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;

import javax.inject.Inject;

public class MessageImageFullscreenPresenter extends FullScreenPresenter<PhotoAttachment,
        MessageImageFullscreenPresenter.View> {

    @ForActivity @Inject Injector injector;
    private ChatTimestampFormatter timestampFormatter;
    protected FlaggingPresenter flaggingPresenter;

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
        flaggingPresenter = view.getFlaggingView().getPresenter();
        //
        view.setShowFlag(photo.isFlaggingEnabled());
        if (photo.getDate() != null) {
            String dateLabel = timestampFormatter.getMessageTimestamp(photo.getDate().getTime());
            view.setDateLabel(dateLabel);
        }
    }

    public void onFlagPressed() {
        flaggingPresenter.flagMessage(photo.getConversationId(), photo.getMessageId());
    }

    public interface View extends FullScreenPresenter.View {
        void setDateLabel(String dateLabel);

        FlaggingView getFlaggingView();

        void setShowFlag(boolean showFlag);
    }

}

package com.messenger.ui.presenter;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.ui.model.AttachmentMenuItem;
import com.messenger.ui.view.chat.ChatScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

public interface ChatScreenPresenter extends MessengerPresenter<ChatScreen, ChatLayoutViewState> {
    boolean sendMessage(String message);

    void retrySendMessage(DataMessage message);

    void onNextPageReached();

    void onLastVisibleMessageChanged(Cursor cursor, int position);

    void openUserProfile(DataUser user);

    void onAttachmentButtonClick();

    void onAttachmentMenuItemChosen(AttachmentMenuItem attachmentMenuItem);

    void onImageClicked(String attachmentImageId);

    void onImagesPicked(List<BasePhotoPickerModel> images);

    void onMapClicked(LatLng latLng);

    void onShowContextualMenu(DataMessage message);

    void onCopyMessageTextToClipboard(DataMessage message);

    void onTranslateMessage(DataMessage message);

    void onRevertTranslate(DataMessage message);

    void onStartNewChatForMessageOwner(DataMessage message);

    void onFlagMessageAttempt(DataMessage message);
}


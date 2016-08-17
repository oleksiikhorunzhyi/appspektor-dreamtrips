package com.messenger.ui.presenter;

import com.google.android.gms.maps.model.LatLng;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.ui.model.AttachmentMenuItem;
import com.messenger.ui.view.chat.ChatScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;

public interface ChatScreenPresenter extends MessengerPresenter<ChatScreen, ChatLayoutViewState> {
   boolean sendMessage(String message);

   void retryClicked(DataMessage dataMessage);

   void retrySendMessage(DataMessage message);

   void onNextPageReached();

   void openUserProfile(DataUser user);

   void onAttachmentButtonClick();

   void onAttachmentMenuItemChosen(AttachmentMenuItem attachmentMenuItem);

   void onMapClicked(LatLng latLng);

   void onShowContextualMenu(DataMessage message);

   void onCopyMessageTextToClipboard(DataMessage message);

   void onTranslateMessage(DataMessage message);

   void onFlagMessage(DataMessage dataMessage);

   void onRevertTranslate(DataMessage message);

   void onStartNewChatForMessageOwner(DataMessage message);

   void onTimestampViewClicked(int position);

   void onReloadHistoryRequired();
}


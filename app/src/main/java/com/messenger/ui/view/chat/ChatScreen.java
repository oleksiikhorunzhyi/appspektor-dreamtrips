package com.messenger.ui.view.chat;

import android.database.Cursor;
import android.view.Menu;

import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.ui.view.layout.MessengerScreen;

import com.messenger.ui.model.AttachmentMenuItem;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

import rx.Observable;

public interface ChatScreen extends MessengerScreen {

    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setTitle(DataConversation conversation, List<DataUser> users);

    void addTypingUser(DataUser user);

    void setShowMarkUnreadMessage(boolean needShow);

    void removeTypingUser(DataUser uxzser);

    void removeAllTypingUsers();

    void showMessages(Cursor cursor, DataConversation conversation);

    void showAttachmentMenu(AttachmentMenuItem[] items);

    void showFlagsListDialog(DataMessage message, List<Flag> flags);

    void showFlagReasonDialog(DataMessage message, Flag flag);

    void showFlagConfirmationDialog(DataMessage message, Flag flag, String reason);

    void showFlaggingProgressDialog();

    void hideFlaggingProgressDialog();

    void showPhotoPicker();

    void hidePhotoPicker();

    Observable<TextViewTextChangeEvent> getEditMessageObservable();

    void showContextualAction(Menu menu, DataMessage message);

    void enableSendMessageButton(boolean enable);

    void showPickLocationError();
}

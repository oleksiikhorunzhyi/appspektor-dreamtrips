package com.messenger.ui.adapter.inflater.conversation;


import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.ui.adapter.inflater.ViewInflater;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.helper.MessageHelper;
import com.messenger.ui.util.chat.SystemMessageTextProvider;
import com.messenger.util.MessageVersionHelper;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class ConversationLastMessageInflater extends ViewInflater {

    @InjectView(R.id.conversation_last_message_textview)
    TextView lastMessageTextView;
    private SystemMessageTextProvider systemMessageTextProvider;

    public void setLastMessage(DataConversation dataConversation, DataMessage message,
                               DataUser messageAuthor, DataUser recipient,
                               DataUser currentUser,
                               String attachmentType, DataTranslation dataTranslation) {
        systemMessageTextProvider = new SystemMessageTextProvider(context, currentUser.getId());
        CharSequence text = getLastMessageText(dataConversation, message, messageAuthor, recipient,
                currentUser, attachmentType, dataTranslation);
        lastMessageTextView.setText(text);
    }

    private String getLastMessageText(DataConversation dataConversation, DataMessage message,
                                      DataUser messageAuthor, DataUser recipient,
                                      DataUser currentUser,
                                      String attachmentType, DataTranslation dataTranslation) {
        if (attachmentType == null) {
            return createMessageText(dataConversation, message, messageAuthor, recipient, currentUser,
                    attachmentType, dataTranslation);
        }
        switch (attachmentType) {
            case AttachmentType.IMAGE:
                return createImageAttachmentText(message, messageAuthor, currentUser);
            case AttachmentType.LOCATION:
                return createLocationAttachmentText(message, messageAuthor, recipient);
            default:
                return createMessageText(dataConversation, message, messageAuthor, recipient,
                        currentUser, attachmentType, dataTranslation);
        }
    }

    private String createMessageText(DataConversation dataConversation, DataMessage message,
                                     DataUser messageAuthor, DataUser recipient,
                                     DataUser currentUser,
                                     String attachmentType, DataTranslation dataTranslation) {
        String messageText = "";
        if (message.getType() != null) {
            if (MessageHelper.isUserMessage(message)) {
                if (MessageVersionHelper.isUnsupported(attachmentType))
                    messageText = Html.fromHtml(context.getString(R.string.chat_update_proposition)).toString();
                else if (dataTranslation.getTranslateStatus() == TranslationStatus.TRANSLATED)
                    messageText = dataTranslation.getTranslation();
                else messageText = message.getText();

                String messageAuthorName = messageAuthor.getDisplayedName();
                if (TextUtils.equals(message.getFromId(), currentUser.getId())) {
                    messageText = String.format(context.getString(R.string.conversation_list_item_last_message_text_format_you),
                            messageText);
                } else if (ConversationHelper.isGroup(dataConversation) && !TextUtils.isEmpty(messageAuthorName)) {
                    messageText = TextUtils.getTrimmedLength(messageAuthorName) > 0 ? messageAuthorName + ": " + messageText : messageText;
                }
            } else if (MessageHelper.isSystemMessage(message)) {
                messageText = systemMessageTextProvider.getSystemMessageText(dataConversation.getType(), message, messageAuthor, recipient).toString();
            }
        } else if (ConversationHelper.isCleared(dataConversation)) {
            return context.getString(R.string.chat_reload_chat_history_info_text);
        }
        return messageText;
    }

    private String createImageAttachmentText(DataMessage message, DataUser messageAuthor, DataUser currentUser) {
        if (TextUtils.equals(message.getFromId(), currentUser.getId())) {
            return context.getString(R.string.conversation_list_item_last_message_image_format_you);
        } else {
            return messageAuthor.getDisplayedName().trim() + " " + context.getString(R.string.conversation_list_item_last_message_image);
        }
    }

    private String createLocationAttachmentText(DataMessage message, DataUser messageAuthor, DataUser currentUser) {
        if (TextUtils.equals(message.getFromId(), currentUser.getId())) {
            return context.getString(R.string.conversation_list_item_last_message_location_format_you);
        } else {
            return messageAuthor.getDisplayedName().trim() + " "
                    + context.getString(R.string.conversation_list_item_last_message_location);
        }
    }
}

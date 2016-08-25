package com.messenger.ui.adapter.inflater.conversation;


import android.content.Context;
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

public class LastMessageTextProvider {

    private Context context;
    private DataUser currentUser;

    private SystemMessageTextProvider systemMessageTextProvider;

    public LastMessageTextProvider(Context context, DataUser currentUser) {
        this.context = context;
        this.systemMessageTextProvider = new SystemMessageTextProvider(context, currentUser.getId());
        this.currentUser = currentUser;
    }

    public String getLastMessageText(DataConversation conversation, DataMessage message,
                                      DataUser sender, DataUser recipient,
                                      String attachmentType, DataTranslation translation) {
        if (attachmentType == null) {
            return createMessageText(conversation, message, sender, recipient,
                    attachmentType, translation);
        }
        switch (attachmentType) {
            case AttachmentType.IMAGE:
                return createImageAttachmentText(sender);
            case AttachmentType.LOCATION:
                return createLocationAttachmentText(sender);
            default:
                return createMessageText(conversation, message, sender, recipient,
                        attachmentType, translation);
        }
    }

    private String createMessageText(DataConversation conversation, DataMessage message,
                                     DataUser sender, DataUser recipient,
                                     String attachmentType, DataTranslation translation) {
        String messageText = "";
        if (message.getType() != null) {
            if (MessageHelper.isUserMessage(message)) {
                if (MessageVersionHelper.isUnsupported(attachmentType))
                    messageText = Html.fromHtml(context.getString(R.string.chat_update_proposition)).toString();
                else if (translation != null && translation.getTranslateStatus() == TranslationStatus.TRANSLATED)
                    messageText = translation.getTranslation();
                else messageText = message.getText();

                String messageAuthorName = sender.getDisplayedName();
                if (TextUtils.equals(sender.getId(), currentUser.getId())) {
                    messageText = String.format(context.getString(R.string.conversation_list_item_last_message_text_format_you),
                            messageText);
                } else if (ConversationHelper.isGroup(conversation) && !TextUtils.isEmpty(messageAuthorName)) {
                    messageText = TextUtils.getTrimmedLength(messageAuthorName) > 0 ? messageAuthorName + ": " + messageText : messageText;
                }
            } else if (MessageHelper.isSystemMessage(message)) {
                messageText = systemMessageTextProvider.getSystemMessageText(conversation.getType(), message, sender, recipient).toString();
            }
        } else if (ConversationHelper.isCleared(conversation)) {
            return context.getString(R.string.chat_reload_chat_history_info_text);
        }
        return messageText;
    }

    private String createImageAttachmentText(DataUser sender) {
        if (TextUtils.equals(currentUser.getId(), sender.getId())) {
            return context.getString(R.string.conversation_list_item_last_message_image_format_you);
        } else {
            return sender.getDisplayedName().trim() + " " + context.getString(R.string.conversation_list_item_last_message_image);
        }
    }

    private String createLocationAttachmentText(DataUser sender) {
        if (TextUtils.equals(currentUser.getId(), sender.getId())) {
            return context.getString(R.string.conversation_list_item_last_message_location_format_you);
        } else {
            return sender.getDisplayedName().trim() + " "
                    + context.getString(R.string.conversation_list_item_last_message_location);
        }
    }
}

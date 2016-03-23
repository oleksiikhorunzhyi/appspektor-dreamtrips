package com.messenger.ui.adapter.inflater;


import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.util.MessageVersionHelper;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class ConversationLastMessageInflater extends ViewInflater {

    @InjectView(R.id.conversation_last_message_textview)
    TextView lastMessageTextView;

    public void setLastMessage(DataConversation dataConversation, DataMessage message,
                               String messageAuthor, DataUser currentUser,
                               String attachmentType, DataTranslation dataTranslation) {
        boolean hasImageAttachment = TextUtils.equals(attachmentType, AttachmentType.IMAGE);
        lastMessageTextView.setText(hasImageAttachment ?
                createAttachmentText(message, messageAuthor, currentUser) :
                createMessageText(dataConversation, message, messageAuthor, currentUser,
                        attachmentType, dataTranslation));
    }

    private String createMessageText(DataConversation dataConversation, DataMessage message,
                                     String messageAuthor, DataUser currentUser,
                                     String attachmentType, DataTranslation dataTranslation) {
        String messageText = null;
        if (!TextUtils.isEmpty(message.getText())) {
            if (MessageVersionHelper.isUnsupported(message.getVersion(), attachmentType))
                messageText = Html.fromHtml(context.getString(R.string.chat_update_proposition)).toString();
            else if (dataTranslation.getTranslateStatus() == TranslationStatus.TRANSLATED)
                messageText = dataTranslation.getTranslation();
            else messageText = message.getText();

            if (TextUtils.equals(message.getFromId(), currentUser.getId())) {
                messageText = String.format(context.getString(R.string.conversation_list_item_last_message_text_format_you),
                        messageText);
            } else if (ConversationHelper.isGroup(dataConversation) && !TextUtils.isEmpty(messageAuthor)) {
                messageText = TextUtils.getTrimmedLength(messageAuthor) > 0 ? messageAuthor + ": " + messageText : messageText;
            }
        }
        return messageText;
    }

    private String createAttachmentText(DataMessage message, String messageAuthor, DataUser currentUser) {
        if (TextUtils.equals(message.getFromId(), currentUser.getId())) {
            return context.getString(R.string.conversation_list_item_last_message_image_format_you);
        } else {
            return messageAuthor.trim() + " " + context.getString(R.string.conversation_list_item_last_message_image);
        }
    }
}

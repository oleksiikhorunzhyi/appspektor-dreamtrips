package com.messenger.ui.util.chat;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.R;

import static com.messenger.messengerservers.constant.MessageType.SYSTEM_JOIN;
import static com.messenger.messengerservers.constant.MessageType.SYSTEM_KICK;
import static com.messenger.messengerservers.constant.MessageType.SYSTEM_LEAVE;

public class SystemMessageTextProvider {

    private Resources resources;
    private String currentUserId;

    public SystemMessageTextProvider(Context context, String currentUserId) {
        resources = context.getResources();
        this.currentUserId = currentUserId;
    }

    public Spanned getSystemMessageText(String conversationType, DataMessage message, DataUser sender, DataUser recipient) {
        switch (message.getType()) {
            case SYSTEM_KICK:
                return obtainKickSystemMessage(conversationType, sender, recipient);
            case SYSTEM_LEAVE:
                return obtainLeftSystemMessage(sender);
            case SYSTEM_JOIN:
                return obtainJoinSystemMessage(conversationType, sender, recipient);
            default:
                return new SpannableString("");
        }
    }

    private Spanned obtainJoinSystemMessage(String conversationType, DataUser sender, DataUser recipient){
        if (ConversationHelper.isTripChat(conversationType)){
            if(isItMe(recipient)) {
                return Html.fromHtml(resources.getString(R.string.system_message_you_are_added));
            } else {
                return Html.fromHtml(resources.getString(R.string.system_message_is_added, recipient.getDisplayedName()));
            }
        } else {
            return Html.fromHtml(resources.getString(R.string.system_message_added, obtainAdminText(sender),
                    obtainUserTextWithoutCapitalLetter(recipient)));
        }
    }

    private Spanned obtainKickSystemMessage(String conversationType, DataUser sender, DataUser recipient){
        if (ConversationHelper.isTripChat(conversationType)){
            if(isItMe(recipient)) {
                return Html.fromHtml(resources.getString(R.string.system_message_you_are_removed));
            } else {
                return Html.fromHtml(resources.getString(R.string.system_message_is_removed, recipient.getDisplayedName()));
            }
        } else {
            return Html.fromHtml(resources.getString(R.string.system_message_removed, obtainAdminText(sender),
                    obtainUserTextWithoutCapitalLetter(recipient)));
        }
    }

    private Spanned obtainLeftSystemMessage(DataUser sender) {
        return Html.fromHtml(isItMe(sender)? resources.getString(R.string.system_message_you_left_the_chat) :
                resources.getString(R.string.system_message_left, sender.getDisplayedName()));
    }

    private String obtainUserTextWithoutCapitalLetter(DataUser recipient) {
        return isItMe(recipient) ? resources.getString(R.string.system_message_you).toLowerCase() :
                recipient.getDisplayedName();
    }

    private String obtainAdminText(DataUser sender) {
        return isItMe(sender)? resources.getString(R.string.system_message_you) :
                resources.getString(R.string.system_message_admin, sender.getDisplayedName());
    }

    private boolean isItMe(DataUser dataUser) {
        return TextUtils.equals(dataUser.getId(), currentUserId);
    }
}

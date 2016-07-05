package com.messenger.ui.adapter.holder.chat;

import android.content.res.Resources;
import android.database.Cursor;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.storage.dao.MessageDAO;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;
import timber.log.Timber;

import static com.messenger.messengerservers.constant.MessageType.SYSTEM_JOIN;
import static com.messenger.messengerservers.constant.MessageType.SYSTEM_KICK;
import static com.messenger.messengerservers.constant.MessageType.SYSTEM_LEAVE;

@Layout(R.layout.list_item_chat_system_message)
public class SystemMessageViewHolder extends MessageViewHolder {

    @InjectView(R.id.message_container)
    TextView systemMessageText;

    DataUser dataUserRecipient;

    public SystemMessageViewHolder(View itemView) {
        super(itemView);
    }

    public void bindCursor(Cursor cursor) {
        super.bindCursor(cursor);
        dataUserRecipient = convertRecipient(cursor);
        showSystemMessage();
    }

    private DataUser convertRecipient(Cursor cursor) {
        String recipientId = cursor.getString(cursor.getColumnIndex(MessageDAO.USER_ID));
        String recipientFirstName = cursor.getString(cursor.getColumnIndex(MessageDAO.USER_FIRST_NAME));
        String recipientLastName = cursor.getString(cursor.getColumnIndex(MessageDAO.USER_LAST_NAME));
        DataUser user = new DataUser(recipientId);
        user.setFirstName(recipientFirstName);
        user.setLastName(recipientLastName);
        return user;
    }

    public void showSystemMessage() {
        CharSequence systemMessage;
        switch (dataMessage.getType()) {
            case SYSTEM_KICK:
                systemMessage = obtainKickSystemMessage();
                break;
            case SYSTEM_LEAVE:
                systemMessage = obtainLeftSystemMessage();
                break;
            case SYSTEM_JOIN:
                systemMessage = obtainJoinSystemMessage();
                break;
            default:
                systemMessage = new SpannableString("");
                break;
        }

        systemMessageText.setText(systemMessage, TextView.BufferType.SPANNABLE);
    }

    protected Spanned obtainJoinSystemMessage(){
        Resources resources = itemView.getResources();

        if (TextUtils.equals(conversationType, ConversationType.TRIP)){
            return Html.fromHtml(resources.getString(R.string.system_message_is_added, obtainUserTextWithCapitalLetter()));
        } else {
            return Html.fromHtml(resources.getString(R.string.system_message_added, obtainAdminText(), obtainUserTextWithoutCapitalLetter()));
        }
    }

    protected Spanned obtainKickSystemMessage(){
        Resources resources = itemView.getResources();

        if (TextUtils.equals(conversationType, ConversationType.TRIP)){
            return Html.fromHtml(resources.getString(R.string.system_message_is_removed, obtainUserTextWithCapitalLetter()));
        } else {
            return Html.fromHtml(resources.getString(R.string.system_message_removed, obtainAdminText(), obtainUserTextWithoutCapitalLetter()));
        }
    }

    protected Spanned obtainLeftSystemMessage() {
        Resources resources = itemView.getResources();
        return Html.fromHtml(isItMe(dataUserSender)? resources.getString(R.string.system_message_you_left_the_chat) :
            resources.getString(R.string.system_message_left, dataUserSender.getDisplayedName()));
    }

    protected String obtainUserTextWithCapitalLetter() {
        return isItMe(dataUserRecipient) ? itemView.getResources().getString(R.string.system_message_you) :
                dataUserRecipient.getDisplayedName();
    }

    protected String obtainUserTextWithoutCapitalLetter() {
        return isItMe(dataUserRecipient) ? itemView.getResources().getString(R.string.system_message_you).toLowerCase() :
                dataUserRecipient.getDisplayedName();
    }

    protected String obtainAdminText() {
        return isItMe(dataUserSender)? itemView.getResources().getString(R.string.system_message_you) :
                itemView.getResources().getString(R.string.system_message_admin, dataUserSender.getDisplayedName());
    }

    protected boolean isItMe(DataUser dataUser) {
        return TextUtils.equals(dataUser.getId(), currentUserId);
    }
}

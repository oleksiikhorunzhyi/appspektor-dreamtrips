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
import com.messenger.ui.adapter.holder.CursorViewHolder;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

@Layout(R.layout.list_item_chat_system_message)
public class SystemMessageViewHolder extends CursorViewHolder {

    @InjectView(R.id.system_message_text)
    TextView systemMessageText;

    DataUser dataUserRecipient;
    DataUser dataUserSender;
    String conversationType;
    String currentUserId;

    public SystemMessageViewHolder(View itemView) {
        super(itemView);
    }

    public void bindCursor(Cursor cursor) {
        //// TODO: 6/30/16 fetch data entities and call showSystemMessage
        //showSystemMessage();
    }

    public void showSystemMessage() {
        Spanned systemMessage = new SpannableString("");
        //// TODO: 6/30/16 replace all constant with feature message type
        int messageType = 9;
        switch (messageType) {
            case 0:
                systemMessage = obtainKickSystemMessage();
                break;
            case 1:
                systemMessage = obtainLeftSystemMessage();
                break;
            case 2:
                systemMessage = obtainJoinSystemMessage();
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

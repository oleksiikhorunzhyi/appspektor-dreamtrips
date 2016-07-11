package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.messenger.entities.DataUser;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.ui.adapter.inflater.MessageCommonInflater;
import com.messenger.ui.util.chat.SystemMessageTextProvider;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

@Layout(R.layout.list_item_chat_system_message)
public class SystemMessageViewHolder extends MessageViewHolder {

    @InjectView(R.id.chat_system_message_text_view)
    TextView systemMessageTextView;

    protected DataUser dataUserRecipient;
    private MessageCommonInflater userMessageCommonInflater = new MessageCommonInflater(itemView);
    private SystemMessageTextProvider systemMessageTextProvider;

    public SystemMessageViewHolder(View itemView) {
        super(itemView);
    }

    public void bindCursor(Cursor cursor) {
        super.bindCursor(cursor);
        systemMessageTextProvider = new SystemMessageTextProvider(itemView.getContext(), currentUserId);
        dataUserRecipient = convertRecipient(cursor);

        Spanned systemMessageText = systemMessageTextProvider.getSystemMessageText(conversationType,
                dataMessage, dataUserSender, dataUserRecipient);
        systemMessageTextView.setText(systemMessageText, TextView.BufferType.SPANNABLE);

        userMessageCommonInflater.onCellBind(previousMessageIsTheSameType);
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

    @Override
    public View getTimestampClickableView() {
        return systemMessageTextView;
    }
}

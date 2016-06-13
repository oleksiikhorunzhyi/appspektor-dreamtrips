package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.messenger.util.LinkHandlerMovementMethod;
import com.messenger.util.MessageVersionHelper;
import com.messenger.util.TruncateUtils;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;
import butterknife.OnLongClick;

public abstract class TextMessageViewHolder extends MessageViewHolder {

    @InjectView(R.id.chat_message)
    protected TextView messageTextView;

    public TextMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindCursor(Cursor cursor) {
        super.bindCursor(cursor);
        if (MessageVersionHelper.isUnsupported(dataMessage.getVersion(), dataAttachment.getType())) {
            showUnsupportMessage();
        } else {
            messageTextView.setAutoLinkMask(Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);
            showMessage();
        }

        messageTextView.setMovementMethod(LinkHandlerMovementMethod.getInstance());
    }

    @OnLongClick(R.id.chat_message)
    boolean onMessageLongClicked() {
        cellDelegate.onMessageLongClicked(dataMessage);
        return true;
    }

    protected void showMessage() {
        messageTextView.setText(TruncateUtils.truncate(dataMessage.getText(),
                messageTextView.getResources().getInteger(R.integer.messenger_max_message_length)));
    }

    protected void showUnsupportMessage() {
        // Linkify.WEB_URLS mask does not work with <a> HTML links, reset it
        messageTextView.setAutoLinkMask(0);
        messageTextView.setText(Html.fromHtml(itemView.getContext().getString(R.string.chat_update_proposition)));
    }

    @Override
    public View getTimestampClickableView() {
        return messageTextView;
    }

}

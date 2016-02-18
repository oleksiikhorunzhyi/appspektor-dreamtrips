package com.messenger.ui.adapter.holder.chat;

import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.messenger.entities.DataMessage;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public abstract class TextMessageViewHolder extends MessageHolder {

    @InjectView(R.id.chat_message)
    protected TextView messageTextView;

    public TextMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setMessage(DataMessage dataMessage) {
        super.setMessage(dataMessage);
    }

    public void showMessage() {
        messageTextView.setAutoLinkMask(Linkify.WEB_URLS);
        messageTextView.setText(message.getText());
    }

    public void showUnsupportMessage() {
        // Linkify.WEB_URLS mask does not work with <a> HTML links, reset it
        messageTextView.setAutoLinkMask(0);
        messageTextView.setText(Html.fromHtml(itemView.getContext().getString(R.string.chat_update_proposition)));
    }

    @Override
    public View getMessageView() {
        return messageTextView;
    }

    public TextView getMessageTextView() {
        return messageTextView;
    }
}

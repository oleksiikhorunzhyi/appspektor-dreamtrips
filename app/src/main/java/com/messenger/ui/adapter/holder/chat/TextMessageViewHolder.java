package com.messenger.ui.adapter.holder.chat;

import android.text.Html;
import android.text.method.LinkMovementMethod;
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
        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void setMessage(DataMessage dataMessage) {
        super.setMessage(dataMessage);
    }

    public void showMessage() {
        messageTextView.setText(message.getText());
    }

    public void showUnsupportMessage() {
        messageTextView.setText(Html.fromHtml(itemView.getContext().getString(R.string.chat_update_proposition)));
    }

    @Override
    public View getMessageView() {
        return messageTextView;
    }
}

package com.messenger.ui.adapter.holder.chat;

import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public abstract class TextMessageViewHolder extends MessageHolder {

    @InjectView(R.id.chat_message)
    public TextView messageTextView;

    public TextMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public View getViewForClickableTimestamp() {
        return messageTextView;
    }
}

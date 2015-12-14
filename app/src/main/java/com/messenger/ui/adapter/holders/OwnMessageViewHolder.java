package com.messenger.ui.adapter.holders;

import android.view.View;
import android.view.ViewGroup;

public class OwnMessageViewHolder extends MessageHolder {

    public OwnMessageViewHolder(View itemView) {
        super(itemView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) messageTextView.getLayoutParams();
        params.setMargins(freeSpaceForMessageRowOwnMessage, params.topMargin, params.rightMargin,
                params.bottomMargin);
    }
}
package com.messenger.ui.adapter.holders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class UserMessageViewHolder extends MessageHolder {

    @InjectView(R.id.chat_item_avatar)
    public ImageView avatarImageView;
    @InjectView(R.id.chat_username)
    public TextView nameTextView;

    public UserMessageViewHolder(View itemView) {
        super(itemView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) messageTextView.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, freeSpaceForMessageRowUserMessage,
                params.bottomMargin);
    }
}
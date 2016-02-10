package com.messenger.ui.adapter.holder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.messenger.ui.widget.AvatarView;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class ContactViewHolder extends BaseViewHolder {

    @InjectView(R.id.contact_icon)
    AvatarView avatarView;
    @InjectView(R.id.contact_name_textview)
    TextView nameTextView;
    @InjectView(R.id.contact_chat_tick_image_view)
    ImageView tickImageView;

    public ContactViewHolder(View itemView) {
        super(itemView);
    }

    public AvatarView getAvatarView() {
        return avatarView;
    }

    public ImageView getTickImageView() {
        return tickImageView;
    }

    public TextView getNameTextView() {
        return nameTextView;
    }
}

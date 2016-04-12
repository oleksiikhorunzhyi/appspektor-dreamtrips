package com.messenger.ui.adapter.holder.conversation;

import android.view.View;
import android.widget.ImageView;

import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class TripConversationViewHolder extends BaseGroupConversationViewHolder {

    @InjectView(R.id.conversation_group_pic)
    ImageView groupAvatarsView;

    public TripConversationViewHolder(View itemView) {
        super(itemView);
    }
}

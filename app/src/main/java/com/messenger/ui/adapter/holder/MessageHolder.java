package com.messenger.ui.adapter.holder;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public abstract class MessageHolder extends ViewHolder {
    private static final float MESSAGE_SCREEN_WIDTH_SHARE = 0.6f;

    @InjectView(R.id.chat_message)
    public TextView messageTextView;

    // Use this variable as margins that determine free space left in row after message and avatar took up
    // the space needed.
    protected final int freeSpaceForMessageRowOwnMessage;
    protected final int freeSpaceForMessageRowUserMessage;

    public MessageHolder(View itemView) {
        super(itemView);
        Resources res = itemView.getResources();
        int screenWidth = res.getDisplayMetrics().widthPixels;
        int messageWidth = (int)(screenWidth * MESSAGE_SCREEN_WIDTH_SHARE);
        int ownMessageWidth = 2 * res.getDimensionPixelSize(R.dimen.chat_list_item_horizontal_padding)
                + messageWidth;
        freeSpaceForMessageRowOwnMessage = screenWidth - ownMessageWidth;
        int userMessageWidth = ownMessageWidth + res.getDimensionPixelSize(R.dimen.chat_list_item_horizontal_padding)
                + res.getDimensionPixelSize(R.dimen.list_item_small_avatar_image_size);
        freeSpaceForMessageRowUserMessage = screenWidth - userMessageWidth;
    }
}

package com.messenger.ui.adapter.inflater;

import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.FrameLayout;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MessageCommonInflater {
    private final int messageVerticalPadding;

    private final View itemView;

    @InjectView(R.id.chat_message_container)
    public View chatMessageContainer;
    @InjectView(R.id.message_container)
    public FrameLayout messageContainer;

    public MessageCommonInflater(View itemView) {
        this.itemView = itemView;
        ButterKnife.inject(this, itemView);
        //
        Resources res = itemView.getResources();
        messageVerticalPadding = res.getDimensionPixelSize(R.dimen.chat_list_item_row_vertical_padding);
    }

    public void onCellBind(boolean previousMessageFromSameUser,
                           boolean unread,
                           @DrawableRes int backgroundId) {
        updateUnreadStatus(unread);
        messageContainer.setBackgroundResource(backgroundId);
        setPaddings(previousMessageFromSameUser);
    }

    public void updateUnreadStatus(boolean unread) {
        chatMessageContainer.setBackgroundResource(unread
                ? R.color.chat_list_item_read_unread_background
                : R.color.chat_list_item_read_read_background);
    }

    private void setPaddings(boolean previousMessageFromSameUser) {
        itemView.setPadding(itemView.getPaddingLeft(),
                previousMessageFromSameUser ? 0 : messageVerticalPadding,
                itemView.getPaddingRight(),
                itemView.getPaddingBottom());
    }

}

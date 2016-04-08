package com.messenger.ui.adapter.holder.chat;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.ui.adapter.MessagesCursorAdapter;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class OwnTextMessageViewHolder extends TextMessageViewHolder implements MessageHolder.OwnMessageHolder {
    private MessagesCursorAdapter.OnRepeatMessageSend onRepeatMessageSend;

    @InjectView(R.id.iv_message_error)
    View ivMessageError;

    @InjectView(R.id.view_switcher)
    ViewSwitcher viewSwitcher;

    private View.OnClickListener onClickListener = view -> {
        switch (view.getId()) {
            case R.id.iv_message_error:
                if (onRepeatMessageSend != null) {
                    onRepeatMessageSend.onRepeatMessageSend(message);
                    viewSwitcher.showNext();
                }
                break;
        }
    };

    public OwnTextMessageViewHolder(View itemView) {
        super(itemView);
        ivMessageError.setOnClickListener(onClickListener);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) messageTextView.getLayoutParams();
        params.setMargins(freeSpaceForMessageRowOwnMessage, params.topMargin, params.rightMargin,
                params.bottomMargin);
    }

    ///////////////////////////////////////////////////////////////////////////
    // General messages logic
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setBubbleBackground() {
        int backgroundResource;
        if (isPreviousMessageFromTheSameUser) {
            itemView.setPadding(itemView.getPaddingLeft(), 0, itemView.getPaddingRight(), itemView.getPaddingBottom());
            backgroundResource = isSelected? R.drawable.dark_blue_bubble: R.drawable.blue_bubble;
        } else {
            itemView.setPadding(itemView.getPaddingLeft(), rowVerticalMargin, itemView.getPaddingRight(), itemView.getPaddingBottom());
            backgroundResource = isSelected? R.drawable.dark_blue_bubble_comics: R.drawable.blue_bubble_comics;
        }
        messageTextView.setBackgroundResource(backgroundResource);
    }

    @Override
    public void updateMessageStatusUi(boolean needMarkUnreadMessage) {
        boolean visible = message.getStatus() == MessageStatus.ERROR;
        int viewVisible = visible ? View.VISIBLE : View.GONE;
        if (viewVisible != viewSwitcher.getVisibility()) {
            viewSwitcher.setVisibility(viewVisible);
        }
        if (visible && viewSwitcher.getCurrentView().getId() == R.id.progress_bar) {
            viewSwitcher.showPrevious();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Own messages logic
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setOnRepeatMessageListener(MessagesCursorAdapter.OnRepeatMessageSend listener) {
        this.onRepeatMessageSend = listener;
    }
}
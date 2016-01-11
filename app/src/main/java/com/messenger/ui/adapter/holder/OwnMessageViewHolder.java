package com.messenger.ui.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.messenger.ui.adapter.MessagesCursorAdapter;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class OwnMessageViewHolder extends MessageHolder {
    private String messageId;
    private MessagesCursorAdapter.OnRepeatMessageSend onRepeatMessageSend;

    @InjectView(R.id.iv_message_error)
    View ivMessageError;

    @InjectView(R.id.view_switcher)
    ViewSwitcher viewSwitcher;

    private View.OnClickListener onClickListener = view -> {
        switch (view.getId()) {
            case R.id.iv_message_error:
                if (onRepeatMessageSend != null) {
                    onRepeatMessageSend.onRepeatMessageSend(messageId);
                    viewSwitcher.showNext();
                }
                break;
        }
    };

    public OwnMessageViewHolder(View itemView) {
        super(itemView);
        ivMessageError.setOnClickListener(onClickListener);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) messageTextView.getLayoutParams();
        params.setMargins(freeSpaceForMessageRowOwnMessage, params.topMargin, params.rightMargin,
                params.bottomMargin);
    }

    public void visibleError(boolean visible) {
        int viewVisible = visible ? View.VISIBLE : View.GONE;
        if (viewVisible != viewSwitcher.getVisibility()) {
            viewSwitcher.setVisibility(viewVisible);
        }
        if (visible && viewSwitcher.getCurrentView().getId() == R.id.progress_bar) {
            viewSwitcher.showPrevious();
        }
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setOnRepeatMessageSend(MessagesCursorAdapter.OnRepeatMessageSend onRepeatMessageSend) {
        this.onRepeatMessageSend = onRepeatMessageSend;
    }
}
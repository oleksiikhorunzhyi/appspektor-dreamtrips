package com.messenger.ui.adapter.holder;

import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class OwnMessageViewHolder extends MessageHolder {
    @InjectView(R.id.iv_message_error)
    View ivMessageError;

    public OwnMessageViewHolder(View itemView) {
        super(itemView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) messageTextView.getLayoutParams();
        params.setMargins(freeSpaceForMessageRowOwnMessage, params.topMargin, params.rightMargin,
                params.bottomMargin);
    }

    public void visibleError(boolean visible) {
        int viewVisible = visible ? View.VISIBLE : View.GONE;
        if (viewVisible != ivMessageError.getVisibility()) {
            ivMessageError.setVisibility(viewVisible);
        }
    }
}
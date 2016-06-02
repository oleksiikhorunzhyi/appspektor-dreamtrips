package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.view.View;

import com.messenger.messengerservers.constant.MessageStatus;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.list_item_chat_own_text_messsage)
public class OwnTextMessageViewHolder extends TextMessageViewHolder {

    public OwnTextMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindCursor(Cursor cursor) {
        super.bindCursor(cursor);
        updateMessageStatusUi();
    }

    public void updateMessageStatusUi() {
        boolean isError = dataMessage.getStatus() == MessageStatus.ERROR;
        viewRetrySend.setVisibility(isError ? View.VISIBLE : View.GONE);
    }
}
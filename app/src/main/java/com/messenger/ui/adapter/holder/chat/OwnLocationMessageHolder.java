package com.messenger.ui.adapter.holder.chat;

import android.view.View;

import com.messenger.messengerservers.constant.MessageStatus;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.list_item_chat_own_location_message)
public class OwnLocationMessageHolder extends LocationMessageHolder {

    public OwnLocationMessageHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void updateMessageStatusUi() {
        super.updateMessageStatusUi();
        switch (dataMessage.getStatus()) {
            case MessageStatus.SENDING:
                mapView.setAlpha(ALPHA_MESSAGE_SENDING);
                break;
            case MessageStatus.ERROR:
                mapView.setAlpha(ALPHA_MESSAGE_NORMAL);
                break;
        }

        boolean isError = dataMessage.getStatus() == MessageStatus.ERROR;
        viewRetrySend.setVisibility(isError ? View.VISIBLE : View.GONE);
    }
}

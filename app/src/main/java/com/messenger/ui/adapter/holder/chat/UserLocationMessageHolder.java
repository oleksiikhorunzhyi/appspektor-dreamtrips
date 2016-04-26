package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.list_item_chat_user_location_message)
public class UserLocationMessageHolder extends LocationMessageHolder {

    public UserLocationMessageHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindCursor(Cursor cursor) {
        super.bindCursor(cursor);
        liteMapInflater.setOnMapLongClickListener(latLng -> onMessageLongClicked());
    }

}

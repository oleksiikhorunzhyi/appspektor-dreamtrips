package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.view.View;

import com.google.android.gms.maps.MapView;
import com.messenger.entities.DataLocationAttachment;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.ui.adapter.inflater.LiteMapInflater;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public abstract class LocationMessageHolder extends UserMessageViewHolder {

    @InjectView(R.id.lite_map_view)
    MapView mapView;

    protected LiteMapInflater liteMapInflater = new LiteMapInflater();

    public LocationMessageHolder(View itemView) {
        super(itemView);
        liteMapInflater.setView(itemView);
    }

    @Override
    public void bindCursor(Cursor cursor) {
        super.bindCursor(cursor);
        DataLocationAttachment dataLocationAttachment = SqlUtils.convertToModel(true,
                DataLocationAttachment.class, cursor);
        liteMapInflater.setLocation(dataLocationAttachment.getLat(), dataLocationAttachment.getLng());
        liteMapInflater.setOnMapClickListener(cellDelegate::onMapClicked);
        updateMessageStatusUi();
    }

    public void updateMessageStatusUi() {
        switch (dataMessage.getStatus()) {
            case MessageStatus.SENT:
            case MessageStatus.READ:
                mapView.setAlpha(ALPHA_MESSAGE_NORMAL);
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        liteMapInflater.clear();
    }
}

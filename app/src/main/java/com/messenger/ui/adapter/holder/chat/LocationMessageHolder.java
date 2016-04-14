package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.view.View;

import com.messenger.entities.DataLocationAttachment;
import com.messenger.ui.adapter.inflater.LiteMapInflater;
import com.raizlabs.android.dbflow.sql.SqlUtils;

public abstract class LocationMessageHolder extends MessageViewHolder {

    private LiteMapInflater liteMapInflater = new LiteMapInflater();

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
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        liteMapInflater.clear();
    }
}

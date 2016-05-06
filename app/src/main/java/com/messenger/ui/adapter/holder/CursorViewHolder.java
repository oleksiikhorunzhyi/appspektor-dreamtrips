package com.messenger.ui.adapter.holder;

import android.database.Cursor;
import android.view.View;

public abstract class CursorViewHolder extends BaseViewHolder {

    public CursorViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindCursor(Cursor cursor);
}

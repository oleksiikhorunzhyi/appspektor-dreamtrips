package com.messenger.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.holder.ContactViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SwipableContactsCursorAdapter extends ContactCursorAdapter {

    public SwipableContactsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public interface SwipeListener {
        void onSelectionStateChanged(List<User> selectedContacts);
    }

    @Override
    protected void onBindUserHolder(ContactViewHolder holder, Cursor cursor, User user) {
        super.onBindUserHolder(holder, cursor, user);
        holder.getTickImageView().setVisibility(View.GONE);
    }
}

package com.messenger.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.holder.BaseViewHolder;
import com.messenger.ui.adapter.holder.ContactViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CheckableContactsCursorAdapter extends ContactCursorAdapter {

    public interface SelectionListener {
        void onSelectionStateChanged(List<User> selectedContacts);
    }

    private List<User> selectedContacts = new ArrayList<>();
    private SelectionListener selectionListener;

    public CheckableContactsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public void setSelectedContacts(List<User> selectedContacts) {
        this.selectedContacts = selectedContacts;
        notifyDataSetChanged();
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    @Override
    protected void onBindUserHolder(ContactViewHolder holder, Cursor cursor, User user) {
        super.onBindUserHolder(holder, cursor, user);
        holder.getTickImageView().setVisibility(View.VISIBLE);
        holder.getTickImageView().setSelected(selectedContacts.contains(user));
        holder.itemView.setOnClickListener((v) -> {
            if (!selectedContacts.contains(user)) {
                selectedContacts.add(user);
            } else {
                selectedContacts.remove(user);
            }
            if (selectionListener != null) {
                selectionListener.onSelectionStateChanged(selectedContacts);
            }
        });
    }
}

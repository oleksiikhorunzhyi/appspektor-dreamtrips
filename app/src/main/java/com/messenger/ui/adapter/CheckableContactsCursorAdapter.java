package com.messenger.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.holder.BaseViewHolder;
import com.messenger.ui.adapter.holder.ContactViewHolder;
import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.List;

public class CheckableContactsCursorAdapter extends ContactCursorAdapter {

    public interface SelectionListener {
        void onSelectionStateChanged(List<DataUser> selectedContacts);
    }

    private List<DataUser> selectedContacts = new ArrayList<>();
    private SelectionListener selectionListener;

    public CheckableContactsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public void setSelectedContacts(List<DataUser> selectedContacts) {
        this.selectedContacts = selectedContacts;
        notifyDataSetChanged();
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    @Override
    protected void onBindUserHolder(ContactViewHolder holder, Cursor cursor, DataUser user) {
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

    @Override
    public BaseViewHolder createContactViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact,
                parent, false);
        return new ContactViewHolder(itemRow);
    }
}

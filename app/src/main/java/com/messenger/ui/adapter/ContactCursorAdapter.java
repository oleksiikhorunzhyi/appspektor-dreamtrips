package com.messenger.ui.adapter;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.holder.ContactWithHeaderViewHolder;
import com.messenger.ui.adapter.holder.ContactViewHolder;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.List;


public class ContactCursorAdapter extends CursorRecyclerViewAdapter<ContactViewHolder> {

    private static final int VIEW_TYPE_CONTACT = 1;
    private static final int VIEW_TYPE_CONTACT_WITH_HEADER = 2;

    private Context context;
    private List<User> selectedContacts = new ArrayList<>();
    private SelectionListener selectionListener;

    public interface SelectionListener {
        void onSelectionStateChanged(List<User> selectedContacts);
    }

    public ContactCursorAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_CONTACT_WITH_HEADER:
                View sectionRow = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_contact_section_header, parent, false);
                return new ContactWithHeaderViewHolder(sectionRow);
            case VIEW_TYPE_CONTACT:
                View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact,
                        parent, false);
                return new ContactViewHolder(itemRow);
            default:
                throw new IllegalArgumentException("There is no such view type in adapter");
        }
    }

    @Override
    public void onBindViewHolderCursor(ContactViewHolder holder, Cursor cursor) {
        final User user = SqlUtils.convertToModel(true, User.class, cursor);
        holder.getNameTextView().setText(user.getName());
        holder.getAvatarView().setOnline(user.isOnline());
        holder.getTickImageView().setSelected(selectedContacts.contains(user));
        Picasso.with(context)
                .load(user.getAvatarUrl())
                .placeholder(android.R.drawable.ic_menu_compass)
                .into(holder.getAvatarView());

        if (holder instanceof ContactWithHeaderViewHolder) {
            String sectionName = Character.toString(user.getUserName().charAt(0)).toUpperCase();
            ((ContactWithHeaderViewHolder) holder).getSectionNameTextView().setText(sectionName);
        }

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
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_CONTACT_WITH_HEADER;

        Cursor cursor = getCursor();
        int cursorPosition = cursor.getPosition();

        cursor.moveToPosition(position);
        String userName = cursor.getString(cursor.getColumnIndex("userName"));
        cursor.moveToPrevious();
        String prevUserName = cursor.getString(cursor.getColumnIndex("userName"));

        cursor.moveToPosition(cursorPosition);

        return userName.charAt(0) == prevUserName.charAt(0) ? VIEW_TYPE_CONTACT : VIEW_TYPE_CONTACT_WITH_HEADER;
    }

    public void setSelectedContacts(List<User> selectedContacts) {
        this.selectedContacts = selectedContacts;
        notifyDataSetChanged();
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }
}

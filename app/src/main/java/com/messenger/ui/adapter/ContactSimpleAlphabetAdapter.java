package com.messenger.ui.adapter;


import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.holder.ContactViewHolder;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Use this class until sorting logic in ContactCursorAdapter is checked
 * to be working (provided users are sorted alphabetically or fixed if needed)
 */
public class ContactSimpleAlphabetAdapter extends CursorRecyclerViewAdapter<ContactSimpleAlphabetAdapter.SectionUsernameHolder> {

    private Context context;
    private List<User> selectedContacts = new ArrayList<>();
    private SelectionListener selectionListener;

    public interface SelectionListener {
        void onSelectionStateChanged(List<User> selectedContacts);
    }

    public ContactSimpleAlphabetAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

    public static class SectionUsernameHolder extends ContactViewHolder {
        @InjectView(R.id.section_name_textview)
        TextView sectionTextView;
        @InjectView(R.id.section_name_textview_divider)
        View divider;

        public SectionUsernameHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public Cursor swapCursor(Cursor cursor) {
        return swapCursor(cursor, null, null);
    }

    public Cursor swapCursor(Cursor cursor, String filter, String column) {
        if (cursor != null) {
            if (!TextUtils.isEmpty(filter)) {
                cursor = new FilterCursorWrapper(cursor, filter,
                        cursor.getColumnIndexOrThrow(column));
            }
        }
        Cursor cursorToReturn = super.swapCursor(cursor);
        return cursorToReturn;
    }

    @Override
    public SectionUsernameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View sectionRow = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contact_with_section_header, parent, false);
        return new SectionUsernameHolder(sectionRow);
    }

    @Override
    public void onBindViewHolderCursor(ContactSimpleAlphabetAdapter.SectionUsernameHolder holder, Cursor cursor) {
        final User user = SqlUtils.convertToModel(true, User.class, cursor);

        String newLetter = null;
        if (cursor.moveToPrevious()) {
            String oldUsername = cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME));
            String currentUsername = user.getName();
            if (!TextUtils.isEmpty(currentUsername)) {
                if (TextUtils.isEmpty(oldUsername) || oldUsername.charAt(0) != currentUsername.charAt(0)) {
                    newLetter = currentUsername.substring(0, 1);
                }
            }
        }
        if (newLetter != null) {
            holder.divider.setVisibility(View.VISIBLE);
            holder.sectionTextView.setVisibility(View.VISIBLE);
            holder.sectionTextView.setText(newLetter);
        } else {
            holder.divider.setVisibility(View.GONE);
            holder.sectionTextView.setVisibility(View.GONE);
        }

        holder.getNameTextView().setText(user.getName());
        holder.getAvatarView().setOnline(user.isOnline());
        holder.getTickImageView().setSelected(selectedContacts.contains(user));
        Picasso.with(context)
                .load(user.getAvatarUrl())
                .placeholder(android.R.drawable.ic_menu_compass)
                .into(holder.getAvatarView());

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

    public void setSelectedContacts(List<User> selectedContacts) {
        this.selectedContacts = selectedContacts;
        notifyDataSetChanged();
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }
}

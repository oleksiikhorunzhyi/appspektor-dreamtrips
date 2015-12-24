package com.messenger.ui.adapter;


import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.holder.BaseViewHolder;
import com.messenger.ui.adapter.holder.ContactWithHeaderViewHolder;
import com.messenger.ui.adapter.holder.ContactViewHolder;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class ContactCursorAdapter extends CursorRecyclerViewAdapter<BaseViewHolder>
        implements SectionIndexer {

    private static final int VIEW_TYPE_CONTACT = 1;
    private static final int VIEW_TYPE_HEADER = 2;

    private Context context;
    private List<User> selectedContacts = new ArrayList<>();
    private SelectionListener selectionListener;
    private OnAvatarClickListener avatarClickListener;

    private AlphabetIndexer indexer;
    private int[] usedSectionNumbers;
    private Map<Integer, Integer> sectionToOffset;
    private Map<Integer, Integer> sectionToPosition;

    public interface SelectionListener {
        void onSelectionStateChanged(List<User> selectedContacts);
    }

    public interface OnAvatarClickListener {
        void onAvatarClick(User user);
    }

    public ContactCursorAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
        if (cursor != null) {
            buildHeaderSectionsData();
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
        buildHeaderSectionsData();
        return cursorToReturn;
    }

    private boolean buildHeaderSectionsData() {
        if (getCursor() == null) {
            return true;
        }
        indexer = new AlphabetIndexer(getCursor(), getCursor()
                .getColumnIndexOrThrow(User.COLUMN_NAME),
                " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        sectionToPosition = new TreeMap<>();
        sectionToOffset = new HashMap<>();

        final int count = super.getItemCount();

        int i;
        for (i = count - 1; i >= 0; i--) {
            sectionToPosition.put(indexer.getSectionForPosition(i), i);
        }

        i = 0;
        usedSectionNumbers = new int[sectionToPosition.keySet().size()];

        for (Integer section : sectionToPosition.keySet()) {
            sectionToOffset.put(section, i);
            usedSectionNumbers[i] = section;
            i++;
        }

        for (Integer section : sectionToPosition.keySet()) {
            sectionToPosition.put(section, sectionToPosition.get(section) + sectionToOffset.get(section));
        }
        return false;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
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
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        checkDataIsValid();
        final int type = getItemViewType(position);
        if (type == VIEW_TYPE_HEADER) {
            onBindSectionNameViewHolder((ContactWithHeaderViewHolder) holder, position);
        } else if (type == VIEW_TYPE_CONTACT) {
            int cursorPosition = position - sectionToOffset.get(getSectionForPosition(position)) - 1;
            if (!getCursor().moveToPosition(cursorPosition)) {
                throw new IllegalStateException("couldn't move cursor to position " + position);
            }
            onBindViewHolderCursor(holder, getCursor());
        }
    }

    public void onBindSectionNameViewHolder(ContactWithHeaderViewHolder holder, int position) {
        String sectionName = (String) getSections()[getSectionForPosition(position)];
        holder.getSectionNameTextView().setText(sectionName);
    }

    @Override
    public void onBindViewHolderCursor(BaseViewHolder h, Cursor cursor) {
        ContactViewHolder holder = (ContactViewHolder) h;
        final User user = SqlUtils.convertToModel(true, User.class, cursor);
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

        holder.getAvatarView().setOnClickListener(v -> {
            if (avatarClickListener != null) {
                avatarClickListener.onAvatarClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (super.getItemCount() != 0) {
            return super.getItemCount() + usedSectionNumbers.length;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getPositionForSection(getSectionForPosition(position))) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_CONTACT;
    }

    @Override
    public Object[] getSections() {
        return indexer.getSections();
    }

    @Override
    public int getPositionForSection(int section) {
        if (!sectionToOffset.containsKey(section)) {
            int i = 0;
            int maxLength = usedSectionNumbers.length;

            while (i < maxLength && section > usedSectionNumbers[i]) {
                i++;
            }
            if (i == maxLength) return super.getItemCount();

            return indexer.getPositionForSection(usedSectionNumbers[i])
                    + sectionToOffset.get(usedSectionNumbers[i]);
        }

        return indexer.getPositionForSection(section) + sectionToOffset.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        int i = 0;
        int maxLength = usedSectionNumbers.length;

        while (i < maxLength && position >= sectionToPosition.get(usedSectionNumbers[i])) {
            i++;
        }
        return usedSectionNumbers[i - 1];
    }

    public void setSelectedContacts(List<User> selectedContacts) {
        this.selectedContacts = selectedContacts;
        notifyDataSetChanged();
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    public void setAvatarClickListener(OnAvatarClickListener avatarClickListener) {
        this.avatarClickListener = avatarClickListener;
    }
}

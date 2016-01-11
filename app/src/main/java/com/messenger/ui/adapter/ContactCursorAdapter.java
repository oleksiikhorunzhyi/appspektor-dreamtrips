package com.messenger.ui.adapter;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;

import com.messenger.messengerservers.entities.Participant;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.entities.User$Table;
import com.messenger.ui.adapter.MessagesCursorAdapter.OnAvatarClickListener;
import com.messenger.ui.adapter.holder.BaseViewHolder;
import com.messenger.ui.adapter.holder.ContactViewHolder;
import com.messenger.ui.adapter.holder.ContactWithHeaderViewHolder;
import com.messenger.util.Constants;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public abstract class ContactCursorAdapter extends CursorRecyclerViewAdapter<BaseViewHolder>
        implements SectionIndexer {

    private static final int VIEW_TYPE_CONTACT = 1;
    private static final int VIEW_TYPE_HEADER = 2;

    // Put -1 as 0 section is the section corresponding to first letter in alphabet index
    private static final int ADMIN_SECTION_INDEX = -1;
    private static final int ADAPTER_POSITION_ADMIN_SECTION = 0;
    private static final int ADAPTER_POSITION_ADMIN_USER = 1;
    private static final int ADMIN_SECTION_MEMBERS_COUNT = 1;

    private Context context;
    private OnAvatarClickListener avatarClickListener;

    private AlphabetIndexer indexer;
    private int[] usedSectionNumbers;
    private Map<Integer, Integer> sectionToOffset;
    private Map<Integer, Integer> sectionToPosition;

    protected User admin;
    protected boolean adminSectionEnabled;

    public ContactCursorAdapter(Context context, Cursor cursor) {
        super(null);
        this.context = context;
        swapCursor(cursor);
    }

    @Override
    public Cursor swapCursor(Cursor cursor) {
        return swapCursor(cursor, null, null);
    }

    public Cursor swapCursor(Cursor cursor, String filter, String column) {
        if (cursor != null) {
            int adminPositionInCursor = -1;
            if (adminSectionEnabled) {
                if (cursor.moveToFirst()) {
                    do {
                        Participant participant = Participant.from(cursor);
                        if (participant.getAffiliation().equals(Participant.Affiliation.OWNER)) {
                            admin = participant.getUser();
                            adminPositionInCursor = cursor.getPosition();
                        }
                    } while (cursor.moveToNext());
                }
                if (admin == null) {
                    adminSectionEnabled = false;
                    adminPositionInCursor = -1;
                }
            }
            int columnIndex = column == null ? 0 : cursor.getColumnIndexOrThrow(column);
            if (adminPositionInCursor >= 0) {
                // hide admin from adapter cursor to prevent its row being shown along
                // with other rows and alphabet section appearing in case there is only
                // admin in section
                cursor = new FilterCursorWrapper(cursor, filter,
                        columnIndex, new int[]{adminPositionInCursor});
            } else {
                cursor = new FilterCursorWrapper(cursor, filter, columnIndex);
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
                .getColumnIndexOrThrow(User$Table.USERNAME),
                " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        sectionToPosition = new TreeMap<>();
        sectionToOffset = new HashMap<>();
        if (adminSectionEnabled) {
            sectionToPosition.put(ADMIN_SECTION_INDEX, ADAPTER_POSITION_ADMIN_SECTION);
        }

        final int count = super.getItemCount();

        int i;
        for (i = count - 1; i >= 0; i--) {
            sectionToPosition.put(indexer.getSectionForPosition(i), i);
        }

        i = 0;
        int sectionsCount = sectionToPosition.keySet().size();

        usedSectionNumbers = new int[sectionsCount];
        if (adminSectionEnabled) {
            sectionToOffset.put(ADMIN_SECTION_INDEX, 0);
            usedSectionNumbers[i] = ADMIN_SECTION_INDEX;
        }
        int adminSectionOffset = adminSectionEnabled ? ADMIN_SECTION_MEMBERS_COUNT : 0;
        for (Integer section : sectionToPosition.keySet()) {
            sectionToOffset.put(section, i + adminSectionOffset);
            usedSectionNumbers[i] = section;
            i++;
        }

        if (adminSectionEnabled) {
            sectionToPosition.put(ADMIN_SECTION_INDEX, sectionToPosition.get(ADMIN_SECTION_INDEX)
                    + sectionToOffset.get(ADMIN_SECTION_INDEX));
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
                return createContactViewHolder(parent, viewType);
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
            if (adminSectionEnabled && position == ADAPTER_POSITION_ADMIN_USER) {
                onBindUserHolder((ContactViewHolder) holder, getCursor(), admin);
            } else {
                int cursorPosition = position - sectionToOffset.get(getSectionForPosition(position)) - 1;
                if (!getCursor().moveToPosition(cursorPosition)) {
                    throw new IllegalStateException("couldn't move cursor to position " + cursorPosition);
                }
                onBindViewHolderCursor(holder, getCursor());
            }
        }
    }

    public void onBindSectionNameViewHolder(ContactWithHeaderViewHolder holder, int position) {
        String sectionName;
        if (adminSectionEnabled && position == ADAPTER_POSITION_ADMIN_SECTION) {
            sectionName = context.getString(R.string.edit_chat_members_admin_section);
        } else {
            sectionName = (String) getSections()[getSectionForPosition(position)];
        }
        holder.getSectionNameTextView().setText(sectionName);
    }

    @Override
    public void onBindViewHolderCursor(BaseViewHolder h, Cursor cursor) {
        ContactViewHolder holder = (ContactViewHolder) h;
        User user = SqlUtils.convertToModel(true, User.class, cursor);
        onBindUserHolder(holder, cursor, user);
    }

    protected void onBindUserHolder(ContactViewHolder holder, Cursor cursor, User user) {
        holder.getNameTextView().setText(user.getName());
        holder.getAvatarView().setOnline(user.isOnline());
        Picasso.with(context)
                .load(user.getAvatarUrl())
                .placeholder(Constants.PLACEHOLDER_USER_AVATAR_SMALL)
                .into(holder.getAvatarView());

        holder.getAvatarView().setOnClickListener(v -> {
            if (avatarClickListener != null) {
                avatarClickListener.onAvatarClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (super.getItemCount() != 0) {
            int adminSectionOffset = adminSectionEnabled ? ADMIN_SECTION_MEMBERS_COUNT : 0;
            return super.getItemCount() + usedSectionNumbers.length + adminSectionOffset;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (adminSectionEnabled && position <= ADAPTER_POSITION_ADMIN_USER) {
            if (position == ADAPTER_POSITION_ADMIN_SECTION) {
                return VIEW_TYPE_HEADER;
            } else {
                return VIEW_TYPE_CONTACT;
            }
        }
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
        if (adminSectionEnabled && (position == ADAPTER_POSITION_ADMIN_SECTION
                || position == ADAPTER_POSITION_ADMIN_USER)) {
            return ADMIN_SECTION_INDEX;
        }

        int i = 0;
        int maxLength = usedSectionNumbers.length;

        while (i < maxLength && position >= sectionToPosition.get(usedSectionNumbers[i])) {
            i++;
        }
        return usedSectionNumbers[i - 1];
    }

    public void setAvatarClickListener(OnAvatarClickListener avatarClickListener) {
        this.avatarClickListener = avatarClickListener;
    }

    public void setAdminSectionEnabled(boolean adminSectionEnabled) {
        this.adminSectionEnabled = adminSectionEnabled;
    }

    public abstract BaseViewHolder createContactViewHolder(ViewGroup parent, int viewType);
}

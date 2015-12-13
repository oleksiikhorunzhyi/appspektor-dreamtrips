package com.messenger.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;
import com.messenger.model.ChatUser;
import com.messenger.ui.widget.AvatarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.InjectView;
import butterknife.ButterKnife;

public class ChatContactsAdapter extends RecyclerView.Adapter<ChatContactsAdapter.ViewHolder> {

    private static final int VIEW_TYPE_CONTACT = 1;
    private static final int VIEW_TYPE_SECTION_HEADER = 2;

    public interface SelectionListener {
        void onSelectionStateChanged(List<ChatUser> selectedContacts);
    }

    private Context context;

    private List<Entry> entries = new ArrayList<>();
    private List<ChatUser> selectedContacts = new ArrayList<>();
    private SelectionListener selectionListener;

    public static class ContactHolder extends ChatContactsAdapter.ViewHolder  {
        @InjectView(R.id.contact_icon) AvatarView avatarView;
        @InjectView(R.id.contact_name_textview) TextView nameTextView;
        @InjectView(R.id.contact_chat_tick_image_view) ImageView tickImageView;

        public ContactHolder(View itemView) {
            super(itemView);
        }
    }

    public static class SectionHeaderHolder extends ChatContactsAdapter.ViewHolder {
        @InjectView(R.id.section_name_textview) TextView sectionNameTextView;

        public SectionHeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            this.itemView = itemView;
        }
    }

    private static class Entry {
        int viewType;
        String sectionName;
        ChatUser user;

        public static Entry newCategoryEntry(String sectionName) {
            Entry entry = new Entry();
            entry.viewType = VIEW_TYPE_SECTION_HEADER;
            entry.sectionName = sectionName;
            return entry;
        }

        public static Entry newContactEntry(ChatUser user) {
            Entry entry = new Entry();
            entry.viewType = VIEW_TYPE_CONTACT;
            entry.user = user;
            return entry;
        }
    }

    public ChatContactsAdapter(Context context) {
        this.context = context;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_SECTION_HEADER:
                View sectionRow = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_contact_section_header, parent, false);
                return new SectionHeaderHolder(sectionRow);
            case VIEW_TYPE_CONTACT:
                View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact,
                        parent, false);
                return new ContactHolder(itemRow);
            default:
                throw new IllegalArgumentException("There is no such view type in adapter");
        }
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
        Entry entry = entries.get(position);
        switch (entry.viewType) {
            case VIEW_TYPE_SECTION_HEADER:
                bindSectionHeaderHolder((SectionHeaderHolder)holder, entry.sectionName);
                break;
            case VIEW_TYPE_CONTACT:
                bindContactHolder((ContactHolder)holder, entry.user);
                break;
        }
    }

    private void bindContactHolder(ContactHolder holder, final ChatUser user) {
        Picasso.with(context).load(user.getAvatarUrl()).placeholder(android.R.drawable.ic_menu_compass)
                .into(holder.avatarView);
        holder.nameTextView.setText(user.getName());
        holder.avatarView.setOnline(user.isOnline());
        holder.tickImageView.setSelected(selectedContacts.contains(user));
        holder.itemView.setOnClickListener((v) -> {
            if (!selectedContacts.contains(user)) {
                selectedContacts.add(user);
            } else {
                selectedContacts.remove(user);
            }
            if (selectionListener != null) {
                selectionListener.onSelectionStateChanged(selectedContacts);
            }
            notifyDataSetChanged();
        });
    }

    private void bindSectionHeaderHolder(SectionHeaderHolder holder, String sectionName) {
        holder.sectionNameTextView.setText(sectionName);
    }

    @Override public int getItemCount() {
        return entries.size();
    }

    @Override public int getItemViewType(int position) {
        return entries.get(position).viewType;
    }

    public void setContacts(List<ChatUser> chatUsers) {
        entries = new ArrayList<>();

        if (chatUsers == null || chatUsers.isEmpty()) {
            return;
        }

        ArrayList<ChatUser> closeFriends = new ArrayList<>();
        for (ChatUser chatUser : chatUsers) {
            if (chatUser.isCloseFriend()) {
                closeFriends.add(chatUser);
            }
        }

        if (!closeFriends.isEmpty()) {
            entries.add(Entry.newCategoryEntry(context.getString(R.string.new_chat_list_item_section_close_friends)));
            for (ChatUser user : closeFriends) {
                entries.add(Entry.newContactEntry(user));
            }
        }

        // sort array of users in natural order of user name, prepare for
        // alphabet based sections
        // TODO Check if it should be sorted by default and this step can be avoided
        ArrayList<ChatUser> sortedContacts = new ArrayList<>();
        sortedContacts.addAll(chatUsers);
        Collections.sort(sortedContacts, (firstUser, secondUser) -> {
            return firstUser.getName().compareTo(secondUser.getName());
        });

        // split sorted array to letters
        // use Strings, not chars, for Unicode support
        String previousFirstLetter = null;
        for (ChatUser user : sortedContacts) {
            String currentFirstLetter = user.getName().substring(0, 1);
            boolean newLetter = previousFirstLetter == null || !previousFirstLetter.equals(currentFirstLetter);
            if (newLetter) {
                entries.add(Entry.newCategoryEntry(String.valueOf(currentFirstLetter)));
            }
            entries.add(Entry.newContactEntry(user));
            previousFirstLetter = currentFirstLetter;
        }

        notifyDataSetChanged();
    }

    public void setSelectedContacts(List<ChatUser> selectedContacts) {
        this.selectedContacts = selectedContacts;
        notifyDataSetChanged();
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }
}

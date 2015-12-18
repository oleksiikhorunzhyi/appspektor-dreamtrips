package com.messenger.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import android.support.v4.content.ContextCompat;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.holder.BaseConversationViewHolder;
import com.messenger.ui.adapter.holder.GroupConversationViewHolder;
import com.messenger.ui.adapter.holder.OneToOneConversationViewHolder;
import com.messenger.util.ChatDateFormatter;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConversationCursorAdapter extends CursorRecyclerViewAdapter<BaseConversationViewHolder> {

    private static final int VIEW_TYPE_ONE_TO_ONE_CONVERSATION = 1;
    private static final int VIEW_TYPE_GROUP_CONVERSATION = 2;

    private Context context;

    private final RecyclerView recyclerView;
    private User currentUser;

    private ClickListener clickListener;

    private ChatDateFormatter chatDateFormatter;

    public interface ClickListener {
        void onConversationClick(Conversation conversation);
    }

    public ConversationCursorAdapter(Context context, RecyclerView recyclerView, User currentUser) {
        super(null);
        this.context = context;
        this.recyclerView = recyclerView;
        this.currentUser = currentUser;

        chatDateFormatter = new ChatDateFormatter(context);
    }

    @Override
    public void onBindViewHolderCursor(BaseConversationViewHolder holder, Cursor cursor) {
        Conversation chatConversation = SqlUtils.convertToModel(true, Conversation.class, cursor);
        setUnreadMessageCount(holder, chatConversation.getUnreadMessageCount());
        setLastMessage(holder, chatConversation.getLastMessage());

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onConversationClick(chatConversation);
            }
        });

        holder.updateParticipants(chatConversation.getId(), arg -> setNameAndAvatar(holder, chatConversation, arg));
    }

    private void setUnreadMessageCount(BaseConversationViewHolder holder, int unreadMessageCount) {
        if (unreadMessageCount > 0) {
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.conversation_list_unread_conversation_bg));
            holder.getUnreadMessagesCountTextView().setVisibility(View.VISIBLE);
            holder.getUnreadMessagesCountTextView().setText(String.valueOf(unreadMessageCount));
        } else {
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.conversation_list_read_conversation_bg));
            holder.getUnreadMessagesCountTextView().setVisibility(View.GONE);
        }
    }

    private void setLastMessage(BaseConversationViewHolder holder, Message lastMessage) {
        if (lastMessage == null) {
            holder.getLastMessageTextView().setVisibility(View.GONE);
            return;
        }
        holder.getLastMessageTextView().setVisibility(View.VISIBLE);
        String messageText = lastMessage.getText();
        if (lastMessage.getFrom().equals(currentUser)) {
            messageText = String.format(context.getString(R.string.conversation_list_item_last_message_format_you), messageText);
        }
        holder.getLastMessageTextView().setText(messageText);
        holder.getLastMessageDateTextView().setText(chatDateFormatter.formatLastConversationMessage(lastMessage.getDate()));
    }

    private void setNameAndAvatar(BaseConversationViewHolder holder, Conversation conversation, List<User> participants) {
        if (isGroupConversation(conversation.getType())) {
            String name = getGroupConversationName(conversation, participants);
            holder.getNameTextView().setText(name);
            if (participants == null || participants.size() == 0) return;
            GroupConversationViewHolder groupHolder = (GroupConversationViewHolder) holder;
            groupHolder.getGroupAvatarsView().updateAvatars(participants);
        } else {
            // TODO: 12/17/15
            if (participants == null || participants.size() == 0) return;
            holder.getNameTextView().setText(getOneToOneConversationName(conversation, participants));
            User addressee = participants.get(0);
            OneToOneConversationViewHolder oneToOneHolder = (OneToOneConversationViewHolder) holder;
            Picasso.with(context)
                    .load(addressee.getAvatarUrl())
                    .placeholder(android.R.drawable.ic_menu_compass)
                    .into(oneToOneHolder.getAvatarView());
            oneToOneHolder.getAvatarView().setOnline(addressee.isOnline());
        }
    }

    private String getGroupConversationName(Conversation conversation, List<User> participants) {
        // TODO: Remove this debug code
        if (participants == null || participants.isEmpty()) {
            return "Group Conv " + conversation.getId();
        }
        if (!TextUtils.isEmpty(conversation.getSubject())) {
            return conversation.getSubject();
        } else {
            StringBuilder sb = new StringBuilder();
            for (Iterator<User> it = participants.iterator(); it.hasNext(); ) {
                sb.append(it.next().getName());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            return sb.toString();
        }
    }

    private String getOneToOneConversationName(Conversation conversation, List<User> participants) {
        User addressee = participants.get(0);
        return addressee.getName() + " " + addressee.getId();
    }

    @Override
    public BaseConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ONE_TO_ONE_CONVERSATION:
                View oneToOneLayout = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_conversation_one_to_one, parent, false);
                return new OneToOneConversationViewHolder(oneToOneLayout);
            case VIEW_TYPE_GROUP_CONVERSATION:
                View groupChatLayout = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_conversation_group, parent, false);
                return new GroupConversationViewHolder(groupChatLayout);
        }
        throw new IllegalStateException("There is no such view type in adapter");
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        int previousPosition = cursor.getPosition();

        if (!getCursor().moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        String type = cursor.getString(cursor.getColumnIndex("type"));
        cursor.moveToPosition(previousPosition);

        return isGroupConversation(type) ? VIEW_TYPE_GROUP_CONVERSATION : VIEW_TYPE_ONE_TO_ONE_CONVERSATION;
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }

    public void swapCursor(Cursor newCursor, String filter) {
        if (TextUtils.isEmpty(filter)) {
            swapCursor(newCursor);
            return;
        }
        Observable.defer(() -> {
            String query = "SELECT * FROM Users u " +
                    "JOIN ParticipantsRelationship p " +
                    "ON p.userId = u._id " +
                    "WHERE p.conversationId = ?";
            return Observable.from(SqlUtils.convertToList(Conversation.class, newCursor))
                    .map(c -> new Pair<>(c, SqlUtils.queryList(User.class, query, c.getId())));

        }).toMap(p -> p.first, p -> p.second)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindView(recyclerView))
                .subscribe(map -> super.swapCursor(new FilterCursorWrapper(newCursor, filter, map)))
        ;
    }

    private boolean isGroupConversation(String conversationType) {
        return !conversationType.equalsIgnoreCase(Conversation.Type.CHAT);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class FilterCursorWrapper extends CursorWrapper {
        private int[] index;
        private int count;
        private int pos;

        public FilterCursorWrapper(Cursor cursor, String filter, Map<Conversation, List<User>> map) {
            super(cursor);
            filter = filter.toLowerCase();

            if (!Objects.equals(filter, "")) {
                this.count = super.getCount();
                this.index = new int[this.count];
                for (int i = 0; i < this.count; i++) {
                    super.moveToPosition(i);
                    Conversation conversation
                            = SqlUtils.convertToModel(true, Conversation.class, cursor);
                    String conversationName;
                    if (isGroupConversation(conversation.getType())) {
                        conversationName = getGroupConversationName(conversation, map.get(conversation));
                    } else {
                        conversationName = getOneToOneConversationName(conversation, map.get(conversation));
                    }
                    if (conversationName.toLowerCase().contains(filter)) {
                        this.index[this.pos++] = i;
                    }
                }
                this.count = this.pos;
                this.pos = 0;
                super.moveToFirst();
            } else {
                this.count = super.getCount();
                this.index = new int[this.count];
                for (int i = 0; i < this.count; i++) {
                    this.index[i] = i;
                }
            }
        }

        @Override
        public boolean move(int offset) {
            return this.moveToPosition(this.pos + offset);
        }

        @Override
        public boolean moveToNext() {
            return this.moveToPosition(this.pos + 1);
        }

        @Override
        public boolean moveToPrevious() {
            return this.moveToPosition(this.pos - 1);
        }

        @Override
        public boolean moveToFirst() {
            return this.moveToPosition(0);
        }

        @Override
        public boolean moveToLast() {
            return this.moveToPosition(this.count - 1);
        }

        @Override
        public boolean moveToPosition(int position) {
            return !(position >= this.count || position < 0) && super.moveToPosition(this.index[position]);
        }

        @Override
        public int getCount() {
            return this.count;
        }

        @Override
        public int getPosition() {
            return this.pos;
        }
    }
}

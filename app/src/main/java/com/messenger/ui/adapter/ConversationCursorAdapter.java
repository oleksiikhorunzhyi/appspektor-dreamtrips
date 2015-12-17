package com.messenger.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
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
import com.worldventures.dreamtrips.R;

import java.util.List;

public class ConversationCursorAdapter extends CursorRecyclerViewAdapter<BaseConversationViewHolder> {

    private static final int VIEW_TYPE_ONE_TO_ONE_CONVERSATION = 1;
    private static final int VIEW_TYPE_GROUP_CONVERSATION = 2;

    private Context context;

    private User currentUser;

    private ClickListener clickListener;

    private ChatDateFormatter chatDateFormatter;

    public interface ClickListener {
        void onConversationClick(Conversation conversation);
    }

    public ConversationCursorAdapter(Context context, Cursor cursor, User currentUser) {
        super(cursor);
        this.context = context;
        this.currentUser = currentUser;

        chatDateFormatter = new ChatDateFormatter(context);
    }

    @Override
    public void onBindViewHolderCursor(BaseConversationViewHolder holder, Cursor cursor) {
        Conversation chatConversation = SqlUtils.convertToModel(true, Conversation.class, cursor);
        setUnreadMessageCount(holder, chatConversation.getUnreadMessageCount());
        setLastMessage(holder, chatConversation.getLastMessage());
        setAvatar(holder, chatConversation);

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onConversationClick(chatConversation);
            }
        });

    }

    private void setUnreadMessageCount(BaseConversationViewHolder holder, int unreadMessageCount){
        if (unreadMessageCount > 0) {
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.conversation_list_unread_conversation_bg));
            holder.getUnreadMessagesCountTextView().setVisibility(View.VISIBLE);
            holder.getUnreadMessagesCountTextView().setText(String.valueOf(unreadMessageCount));
        } else {
            holder.itemView.setBackgroundColor(context.getResources()
                    .getColor(R.color.conversation_list_read_conversation_bg));
            holder.getUnreadMessagesCountTextView().setVisibility(View.INVISIBLE);
        }
    }

    private void setLastMessage(BaseConversationViewHolder holder, Message lastMessage){
        if (lastMessage == null) return;
        String messageText = lastMessage.getText();
        if (lastMessage.getFrom().equals(currentUser)) {
            messageText = String.format(context.getString(R.string.conversation_list_item_last_message_format_you), messageText);
        }
        holder.getLastMessageTextView().setText(messageText);
        holder.getLastMessageDateTextView().setText(chatDateFormatter.formatLastConversationMessage(lastMessage.getDate()));
    }

    private void setAvatar(BaseConversationViewHolder holder, Conversation conversation){
        List<User> participants = conversation.getParticipants();


        if (participants == null || participants.size() == 0) return;

        if (isGroupConversation(conversation.getType())) {
            holder.getNameTextView().setText(conversation.getSubject());
            GroupConversationViewHolder groupHolder = (GroupConversationViewHolder) holder;
            groupHolder.getGroupAvatarsView().updateAvatars(participants);
        } else {
            User addressee = participants.get(0);
            holder.getNameTextView().setText(addressee.getName());
            OneToOneConversationViewHolder oneToOneHolder = (OneToOneConversationViewHolder) holder;
            Picasso.with(context)
                    .load(addressee.getAvatarUrl())
                    .placeholder(android.R.drawable.ic_menu_compass)
                    .into(oneToOneHolder.getAvatarView());
            oneToOneHolder.getAvatarView().setOnline(addressee.isOnline());
        }
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

    private boolean isGroupConversation(String conversationType) {
        return !conversationType.equalsIgnoreCase(Conversation.Type.CHAT);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

}

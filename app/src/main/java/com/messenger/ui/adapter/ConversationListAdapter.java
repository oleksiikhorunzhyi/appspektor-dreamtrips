package com.messenger.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;
import com.messenger.app.Environment;
import com.messenger.model.ChatConversation;
import com.messenger.model.ChatMessage;
import com.messenger.model.ChatUser;
import com.messenger.ui.widget.AvatarView;
import com.messenger.ui.widget.GroupAvatarsView;
import com.messenger.util.ChatDateFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.ButterKnife;

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ViewHolder> {

    private static final int VIEW_TYPE_ONE_TO_ONE_CONVERSATION = 1;
    private static final int VIEW_TYPE_GROUP_CONVERSATION = 2;

    public interface ClickListener {
        void onConversationClick(ChatConversation conversation);
    }

    private Context context;

    private ClickListener clickListener;

    private List<ChatConversation> conversationList;

    private ChatDateFormatter chatDateFormatter;

    public ConversationListAdapter(Context context) {
        this.context = context;
        chatDateFormatter = new ChatDateFormatter(context);
        ButterKnife.inject(this, (Activity) context);
    }

    public static class OneToOneConversationViewHolder extends ViewHolder {
        @InjectView(R.id.conversation_avatar_view) AvatarView avatarView;

        public OneToOneConversationViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class GroupConversationViewHolder extends ViewHolder {
        @InjectView(R.id.conversation_group_avatars_view) GroupAvatarsView groupAvatarsView;

        public GroupConversationViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        @InjectView(R.id.conversation_name_textview) TextView nameTextView;
        @InjectView(R.id.conversation_last_message_textview) TextView lastMessageTextView;
        @InjectView(R.id.conversation_last_message_date_textview) TextView lastMessageDateTextView;
        @InjectView(R.id.conversation_unread_messages_count_textview) TextView unreadMessagesCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.inject(this, itemView);
        }
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        final ChatConversation chatConversation = conversationList.get(position);

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onConversationClick(chatConversation);
            }
        });

        boolean hasChatMessages = chatConversation.getMessages() != null && chatConversation.getMessages().size() > 0;
        if (hasChatMessages) {
            ChatMessage lastMessage = chatConversation.getMessages().get(chatConversation.getMessages().size() - 1);

            if (lastMessage.getUser().equals(Environment.getCurrentUser())) {
                String lastMessageText = String.format(context
                                .getString(R.string.conversation_list_item_last_message_format_you),
                                    lastMessage.getMessage());
                holder.lastMessageTextView.setText(lastMessageText);
            } else {
                holder.lastMessageTextView.setText(lastMessage.getMessage());
            }
            holder.lastMessageDateTextView.setText(chatDateFormatter.formatLastConversationMessage(lastMessage));
        } else {
            holder.lastMessageTextView.setText("");
            holder.lastMessageDateTextView.setText("");
        }

        if (chatConversation.getUnreadMessagesCount() > 0) {
            holder.itemView.setBackgroundColor(context.getResources()
                    .getColor(R.color.conversation_list_unread_conversation_bg));
            holder.unreadMessagesCountTextView.setVisibility(View.VISIBLE);
            holder.unreadMessagesCountTextView.setText(String.valueOf(chatConversation.getUnreadMessagesCount()));
        } else {
            holder.itemView.setBackgroundColor(context.getResources()
                    .getColor(R.color.conversation_list_read_conversation_bg));
            holder.unreadMessagesCountTextView.setVisibility(View.INVISIBLE);
        }

        //  1 to 1 or group specific logic
        if (chatConversation.isGroupConversation()) {
            holder.nameTextView.setText(chatConversation.getConversationName());
            GroupConversationViewHolder groupHolder = (GroupConversationViewHolder)holder;
            groupHolder.groupAvatarsView.updateAvatars(chatConversation.getChatUsers());
        } else {
            ChatUser addressee = chatConversation.getChatUsers().get(1);
            holder.nameTextView.setText(addressee.getName());
            OneToOneConversationViewHolder oneToOneHolder = (OneToOneConversationViewHolder)holder;
            Picasso.with(context).load(addressee
                    .getAvatarUrl()).placeholder(android.R.drawable.ic_menu_compass)
                    .into(oneToOneHolder.avatarView);
            oneToOneHolder.avatarView.setOnline(addressee.isOnline());
        }
    }

    @Override public int getItemViewType(int position) {
        ChatConversation chatConversation = conversationList.get(position);
        return chatConversation.isGroupConversation() ? VIEW_TYPE_GROUP_CONVERSATION : VIEW_TYPE_ONE_TO_ONE_CONVERSATION;
    }

    @Override public int getItemCount() {
        if (conversationList == null || conversationList.isEmpty()) {
            return 0;
        }
        return conversationList.size();
    }

    public void setConversationList(List<ChatConversation> conversationList, boolean showGroupChatsOnly) {
        if (showGroupChatsOnly) {
            this.conversationList = new ArrayList<>();
            for (ChatConversation chatConversation : conversationList) {
                if (chatConversation.isGroupConversation()) {
                    this.conversationList.add(chatConversation);
                }
            }
        }
        else {
            this.conversationList = conversationList;
        }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}

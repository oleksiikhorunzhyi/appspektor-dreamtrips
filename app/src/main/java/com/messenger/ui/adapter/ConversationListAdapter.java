package com.messenger.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.messenger.ui.adapter.holder.BaseConversationViewHolder;
import com.messenger.ui.adapter.holder.GroupConversationViewHolder;
import com.messenger.ui.adapter.holder.OneToOneConversationViewHolder;
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

public class ConversationListAdapter extends RecyclerView.Adapter<BaseConversationViewHolder> {

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

    @Override public BaseConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

    @Override public void onBindViewHolder(BaseConversationViewHolder holder, int position) {
        final ChatConversation chatConversation = conversationList.get(position);

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onConversationClick(chatConversation);
            }
        });

//        boolean hasChatMessages = chatConversation.getMessages() != null && chatConversation.getMessages().size() > 0;
//        if (hasChatMessages) {
//            ChatMessage lastMessage = //chatConversation.getMessages().get(chatConversation.getMessages().size() - 1);
//
//            if (lastMessage.getUser().equals(Environment.getCurrentUser())) {
//                String lastMessageText = String.format(context
//                                .getString(R.string.conversation_list_item_last_message_format_you),
//                                    lastMessage.getMessage());
//                holder.getLastMessageTextView().setText(lastMessageText);
//            } else {
//                holder.getLastMessageTextView().setText(lastMessage.getMessage());
//            }
//            holder.getLastMessageDateTextView().setText(chatDateFormatter.formatLastConversationMessage(lastMessage));
//        } else {
//            holder.getLastMessageTextView().setText("");
//            holder.getLastMessageDateTextView().setText("");
//        }

        if (chatConversation.getUnreadMessagesCount() > 0) {
            holder.itemView.setBackgroundColor(context.getResources()
                    .getColor(R.color.conversation_list_unread_conversation_bg));
            holder.getUnreadMessagesCountTextView().setVisibility(View.VISIBLE);
            holder.getUnreadMessagesCountTextView().setText(String.valueOf(chatConversation.getUnreadMessagesCount()));
        } else {
            holder.itemView.setBackgroundColor(context.getResources()
                    .getColor(R.color.conversation_list_read_conversation_bg));
            holder.getUnreadMessagesCountTextView().setVisibility(View.INVISIBLE);
        }

        //  1 to 1 or group specific logic
        if (chatConversation.isGroupConversation()) {
            holder.getNameTextView().setText(chatConversation.getConversationName());
         //   GroupConversationViewHolder groupHolder = (GroupConversationViewHolder)holder;
           // groupHolder.getGroupAvatarsView().updateAvatars(chatConversation.getChatUsers());
        } else {
            ChatUser addressee = chatConversation.getChatUsers().get(1);
            holder.getNameTextView().setText(addressee.getName());
            OneToOneConversationViewHolder oneToOneHolder = (OneToOneConversationViewHolder)holder;
            Picasso.with(context).load(addressee
                    .getAvatarUrl()).placeholder(android.R.drawable.ic_menu_compass)
                    .into(oneToOneHolder.getAvatarView());
            oneToOneHolder.getAvatarView().setOnline(addressee.isOnline());
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

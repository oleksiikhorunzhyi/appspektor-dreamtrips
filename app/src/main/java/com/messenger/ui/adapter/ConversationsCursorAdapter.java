package com.messenger.ui.adapter;

import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataConversation$Table;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.ui.adapter.holder.conversation.BaseConversationViewHolder;
import com.messenger.ui.adapter.holder.conversation.ClosedGroupConversationViewHolder;
import com.messenger.ui.adapter.holder.conversation.GroupConversationViewHolder;
import com.messenger.ui.adapter.holder.conversation.OneToOneConversationViewHolder;
import com.messenger.ui.adapter.swipe.SwipeLayoutContainer;
import com.worldventures.dreamtrips.R;

import static com.messenger.messengerservers.constant.ConversationType.CHAT;
import static com.messenger.messengerservers.constant.ConversationType.GROUP;

public class ConversationsCursorAdapter
        extends CursorRecyclerViewAdapter<BaseConversationViewHolder>
        implements SwipeLayoutContainer {

    private static final int VIEW_TYPE_ONE_TO_ONE_CONVERSATION = 1;
    private static final int VIEW_TYPE_GROUP_CONVERSATION = 2;
    private static final int VIEW_TYPE_GROUP_CLOSED_CONVERSATION = 3;

    private SwipeButtonsListener swipeButtonsListener;
    private ConversationClickListener conversationClickListener;

    private String selectedConversationId;

    public interface ConversationClickListener {
        void onConversationClick(DataConversation conversation);
    }

    public interface SwipeButtonsListener {
        void onDeleteButtonPressed(DataConversation conversation);

        void onMoreOptionsButtonPressed(DataConversation conversation);
    }

    public ConversationsCursorAdapter() {
        super(null);
    }

    @Override
    public void onBindViewHolderCursor(BaseConversationViewHolder holder, Cursor cursor) {
        holder.bindCursor(cursor);
        holder.applySelection(selectedConversationId);
        holder.setConversationClickListener(conversationClickListener);
        holder.setSwipeButtonsListener(swipeButtonsListener);
    }

    public void setSelectedConversationId(String selectedConversationId) {
        this.selectedConversationId = selectedConversationId;
        notifyDataSetChanged();
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
            case VIEW_TYPE_GROUP_CLOSED_CONVERSATION:
                View closedGroupChatLayout = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_conversation_group_closed, parent, false);
                return new ClosedGroupConversationViewHolder(closedGroupChatLayout);
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
        final String type = cursor.getString(cursor.getColumnIndex(DataConversation$Table.TYPE));
        boolean abandoned = !TextUtils.equals(cursor.getString(cursor.getColumnIndex(DataConversation$Table.STATUS)), ConversationStatus.PRESENT);
        cursor.moveToPosition(previousPosition);
        switch (type) {
            case CHAT:
                return VIEW_TYPE_ONE_TO_ONE_CONVERSATION;
            case GROUP:
            default:
                if (abandoned) return VIEW_TYPE_GROUP_CLOSED_CONVERSATION;
                return VIEW_TYPE_GROUP_CONVERSATION;
        }
    }

    public void setConversationClickListener(ConversationClickListener conversationClickListener) {
        this.conversationClickListener = conversationClickListener;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Swipe layout
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public void setSwipeButtonsListener(SwipeButtonsListener swipeButtonsListener) {
        this.swipeButtonsListener = swipeButtonsListener;
    }
}

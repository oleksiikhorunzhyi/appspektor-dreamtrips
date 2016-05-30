package com.messenger.ui.adapter;

import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataConversation$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser$Table;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.ui.adapter.holder.conversation.BaseConversationViewHolder;
import com.messenger.ui.adapter.holder.conversation.ClosedGroupConversationViewHolder;
import com.messenger.ui.adapter.holder.conversation.GroupConversationViewHolder;
import com.messenger.ui.adapter.holder.conversation.OneToOneConversationViewHolder;
import com.messenger.ui.adapter.swipe.SwipeLayoutContainer;
import com.messenger.ui.helper.ConversationHelper;
import com.raizlabs.android.dbflow.sql.SqlUtils;
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
        DataConversation conversation = SqlUtils.convertToModel(true, DataConversation.class, cursor);
        String conversationParticipants = null;
        int conversationParticipantsCount = 0;
        if (ConversationHelper.isGroup(conversation) || ConversationHelper.isTripChat(conversation)) {
            String groupChatName = conversation.getSubject();
            if (TextUtils.isEmpty(groupChatName)) {
                conversationParticipants = cursor.getString(cursor.getColumnIndex(ConversationsDAO.GROUP_CONVERSATION_NAME_COLUMN));
            }
            conversationParticipantsCount = cursor.getInt(cursor.getColumnIndex(ConversationsDAO.GROUP_CONVERSATION_USER_COUNT_COLUMN));
        }
        DataMessage message = SqlUtils.convertToModel(true, DataMessage.class, cursor);
        DataTranslation translation = SqlUtils.convertToModel(true, DataTranslation.class, cursor);
        String messageAuthor = cursor.getString(cursor.getColumnIndex(ConversationsDAO.LAST_MESSAGE_AUTHOR_COLUMN));
        String attachmentType = cursor.getString(cursor.getColumnIndex(ConversationsDAO.ATTACHMENT_TYPE_COLUMN));

        holder.bindConversation(conversation, conversationParticipants, conversationParticipantsCount);
        holder.bindLastMessage(message, messageAuthor, attachmentType, translation);
        holder.applySelection(selectedConversationId);
        if (holder instanceof OneToOneConversationViewHolder) {
            bindParticipantData((OneToOneConversationViewHolder) holder, cursor);
        }
        holder.setConversationClickListener(conversationClickListener);
        holder.setSwipeButtonsListener(swipeButtonsListener);
    }

    private void bindParticipantData(OneToOneConversationViewHolder holder, Cursor cursor) {
        String avatar = cursor.getString(cursor.getColumnIndex(DataUser$Table.USERAVATARURL));
        // Database does not have boolean type and store true as 1, false as 0
        boolean online = cursor.getInt(cursor.getColumnIndex(DataUser$Table.ONLINE)) == 1;
        String name = cursor.getString(cursor.getColumnIndex(ConversationsDAO.SINGLE_CONVERSATION_NAME_COLUMN));
        holder.bindUserProperties(name, avatar, online);
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

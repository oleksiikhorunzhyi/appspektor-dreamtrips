package com.messenger.ui.adapter;

import android.database.Cursor;
import android.view.ViewGroup;

import com.messenger.entities.DataConversation;
import com.messenger.ui.adapter.holder.chat.ChatViewHolderProvider;
import com.messenger.ui.adapter.holder.chat.MessageViewHolder;
import com.messenger.ui.util.chat.ChatTimestampAnimator;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class ChatAdapter extends CursorRecyclerViewAdapter<MessageViewHolder> {

    private boolean needMarkUnreadMessages;
    private DataConversation dataConversation;

    @Inject
    ChatViewHolderProvider viewHolderProvider;
    ChatTimestampAnimator chatTimestampAnimator;

    private ChatCellDelegate cellDelegate;

    public ChatAdapter(Cursor cursor, Injector injector) {
        super(cursor);
        injector.inject(this);
        chatTimestampAnimator = new ChatTimestampAnimator(this);
        injector.inject(chatTimestampAnimator);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MessageViewHolder messageViewHolder = viewHolderProvider.provideViewHolder(parent, viewType);
        messageViewHolder.setCellDelegate(cellDelegate);
        return messageViewHolder;
    }

    @Override
    public void onBindViewHolderCursor(MessageViewHolder holder, Cursor cursor) {
        int position = cursor.getPosition();
        chatTimestampAnimator.bindTimeStampIfNeeded(holder, cursor, position);
        holder.setSelected(chatTimestampAnimator.isManualTimestampPosition(position));
        holder.setNeedMarkUnreadMessage(needMarkUnreadMessages);
        holder.setConversation(dataConversation);
        holder.bindCursor(cursor);
    }

    @Override
    public int getItemViewType(int position) {
        return viewHolderProvider.provideViewType(getCursor(), position);
    }

    public void setCellDelegate(ChatCellDelegate cellDelegate) {
        this.cellDelegate = cellDelegate;
    }

    public void setNeedMarkUnreadMessages(boolean needMarkUnreadMessages) {
        this.needMarkUnreadMessages = needMarkUnreadMessages;
    }

    public void setConversation(DataConversation conversation) {
        this.dataConversation = conversation;
    }
}

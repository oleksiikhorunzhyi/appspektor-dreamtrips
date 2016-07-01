package com.messenger.ui.adapter;

import android.database.Cursor;
import android.view.ViewGroup;

import com.messenger.ui.adapter.holder.chat.ChatViewHolderProvider;
import com.messenger.ui.adapter.holder.chat.MessageViewHolder;
import com.messenger.ui.adapter.inflater.chat.ChatTimestampInflater;

import javax.inject.Inject;

public class ChatAdapter extends HeaderableCursorRecyclerViewAdapter<MessageViewHolder> {

    private boolean needMarkUnreadMessages;

    @Inject
    ChatViewHolderProvider viewHolderProvider;
    ChatTimestampInflater chatTimestampInflater;

    private ChatCellDelegate cellDelegate;

    public ChatAdapter(Cursor cursor, ChatTimestampInflater chatTimestampInflater) {
        super(cursor);
        chatTimestampInflater.getClickedTimestampPositionsObservable()
                .subscribe(position -> cellDelegate.onTimestampViewClicked(position));
        this.chatTimestampInflater = chatTimestampInflater;
        this.chatTimestampInflater.setAdapter(this);
    }

    @Override
    public MessageViewHolder onCreateElementViewHolder(ViewGroup parent, int viewType) {
        MessageViewHolder messageViewHolder = viewHolderProvider.provideViewHolder(parent, viewType);
        messageViewHolder.setCellDelegate(cellDelegate);
        return messageViewHolder;
    }

    @Override
    protected void onBindElementViewHolderCursor(MessageViewHolder holder, Cursor cursor) {
        int position = cursor.getPosition();
        holder.setSelected(chatTimestampInflater.bindTimeStampIfNeeded(holder, cursor, position));
        holder.setNeedMarkUnreadMessage(needMarkUnreadMessages);
        holder.bindCursor(cursor);
    }

    @Override
    protected int getElementViewType(int position) {
        return viewHolderProvider.provideViewType(getCursor(), position);
    }

    public void setCellDelegate(ChatCellDelegate cellDelegate) {
        this.cellDelegate = cellDelegate;
    }

    public void setNeedMarkUnreadMessages(boolean needMarkUnreadMessages) {
        this.needMarkUnreadMessages = needMarkUnreadMessages;
    }

    public void refreshTimestampView(int position) {
        chatTimestampInflater.showManualTimestampForPosition(position);
    }
}

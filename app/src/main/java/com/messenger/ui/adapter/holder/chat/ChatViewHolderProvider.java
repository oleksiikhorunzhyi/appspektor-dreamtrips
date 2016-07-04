package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apptentive.android.sdk.module.messagecenter.view.holder.MessageHolder;
import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataUser;
import com.messenger.storage.dao.MessageDAO;
import com.techery.spares.adapter.AdapterHelper;
import com.techery.spares.annotations.Layout;

import java.util.Set;

import javax.inject.Inject;

public class ChatViewHolderProvider {

    private Set<ChatViewHolderInfo> chatViewHolderInfoSet;
    private DataUser currentUser;

    @Inject
    public ChatViewHolderProvider(DataUser currentUser, Set<ChatViewHolderInfo> chatViewHolderInfoSet) {
        this.chatViewHolderInfoSet = chatViewHolderInfoSet;
        this.currentUser = currentUser;
    }

    public int provideViewType(Cursor cursor, int position) {
        cursor.moveToPosition(position);

        boolean own = isOwnMessage(cursor);
        String attachmentType = cursor.getString(cursor.getColumnIndex(MessageDAO.ATTACHMENT_TYPE));

        return Queryable.from(chatViewHolderInfoSet).first(info -> (info.isOwn() == own)
                    && TextUtils.equals(attachmentType, info.getType())).getViewType();
    }

    public MessageViewHolder provideViewHolder(ViewGroup parent, int viewType) {
        ChatViewHolderInfo chatViewHolderInfo = Queryable.from(chatViewHolderInfoSet)
                    .first(info -> info.getViewType() == viewType);
        MessageViewHolder messageHolder = buildCell(chatViewHolderInfo.getViewHolderClass(), parent);
        messageHolder.setOwnMessage(chatViewHolderInfo.isOwn());
        return messageHolder;
    }

    private MessageViewHolder buildCell(Class<? extends MessageViewHolder> holderClass, ViewGroup parent) {
        Layout layoutAnnotation = holderClass.getAnnotation(Layout.class);
        View holderView = inflateRow(parent, layoutAnnotation.value());
        return (MessageViewHolder) AdapterHelper.buildHolder(holderClass, holderView);
    }

    private View inflateRow(ViewGroup parent, int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    private boolean isOwnMessage(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID))
                .equals(currentUser.getId());
    }

}

package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.ui.adapter.ChatCellDelegate;
import com.messenger.ui.adapter.holder.CursorViewHolder;
import com.messenger.ui.adapter.inflater.MessageCommonInflater;
import com.messenger.ui.adapter.inflater.UserMessageHolderInflater;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;

public abstract class MessageViewHolder extends CursorViewHolder {

    protected static final float ALPHA_MESSAGE_SENDING = 0.5f;
    protected static final float ALPHA_MESSAGE_NORMAL = 1f;

    protected DataMessage dataMessage;
    protected DataAttachment dataAttachment;
    protected DataUser dataUserSender;
    protected DataTranslation dataTranslation;
    protected DataConversation dataConversation;

    @InjectView(R.id.chat_date)
    public TextView dateTextView;
    @InjectView(R.id.message_container)
    public FrameLayout messageContainer;
    @Optional
    @InjectView(R.id.view_switcher)
    public ViewSwitcher retrySwitcher;

    protected boolean selected;
    protected boolean previousMessageFromSameUser;
    protected boolean needMarkUnreadMessage;

    protected ChatCellDelegate cellDelegate;

    private final MessageCommonInflater messageCommonInflater;
    private final UserMessageHolderInflater userMessageHolderInflater;

    public MessageViewHolder(View itemView) {
        super(itemView);
        messageCommonInflater = new MessageCommonInflater(itemView);
        userMessageHolderInflater = new UserMessageHolderInflater(itemView);
    }

    @Override
    public void bindCursor(Cursor cursor) {
        previousMessageFromSameUser = previousMessageIsFromSameUser(cursor);
        dataMessage = SqlUtils.convertToModel(true, DataMessage.class, cursor);
        dataAttachment = SqlUtils.convertToModel(true, DataAttachment.class, cursor);
        dataUserSender = SqlUtils.convertToModel(true, DataUser.class, cursor);
        boolean translationExist = !TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(MessageDAO.TRANSLATION_ID)));
        dataTranslation = translationExist ? SqlUtils.convertToModel(true, DataTranslation.class, cursor) : null;
        //
        messageCommonInflater.onCellBind(previousMessageFromSameUser,
                dataMessage.getStatus() == MessageStatus.SENT && needMarkUnreadMessage,
                previousMessageFromSameUser ? provideBackgroundForFollowing() : provideBackgroundForInitial());
        userMessageHolderInflater.onCellBind(dataUserSender, dataConversation, previousMessageFromSameUser);
    }

    @OnLongClick(R.id.message_container)
    boolean onMessageLongClicked() {
        cellDelegate.onMessageLongClicked(dataMessage);
        return true;
    }

    @Optional
    @OnClick(R.id.retry)
    void onRetry() {
        cellDelegate.onRetryClicked(dataMessage);
        retrySwitcher.showNext();
    }

    @Optional
    @OnClick(R.id.chat_item_avatar)
    void onUserAvatarClicked() {
        cellDelegate.onAvatarClicked(dataUserSender);
    }

    public void setCellDelegate(ChatCellDelegate cellDelegate) {
        this.cellDelegate = cellDelegate;
    }

    public void setConversation(DataConversation dataConversation) {
        this.dataConversation = dataConversation;
    }

    public void setNeedMarkUnreadMessage(boolean needMarkUnreadMessage) {
        this.needMarkUnreadMessage = needMarkUnreadMessage;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @DrawableRes
    protected abstract int provideBackgroundForFollowing();

    @DrawableRes
    protected abstract int provideBackgroundForInitial();

    public View getMessageView() {
        return messageContainer;
    }

    private boolean previousMessageIsFromSameUser(Cursor cursor) {
        String currentId = cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID));
        if (!cursor.moveToPrevious()) {
            cursor.moveToNext();
            return false;
        }
        String prevId = cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID));
        cursor.moveToNext();
        return TextUtils.equals(prevId, currentId);
    }

}

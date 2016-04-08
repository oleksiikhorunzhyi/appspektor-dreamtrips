package com.messenger.ui.adapter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.ui.adapter.holder.chat.ImageMessageViewHolder;
import com.messenger.ui.adapter.holder.chat.MessageHolder;
import com.messenger.ui.adapter.holder.chat.OwnImageMessageViewHolder;
import com.messenger.ui.adapter.holder.chat.OwnTextMessageViewHolder;
import com.messenger.ui.adapter.holder.chat.TextMessageViewHolder;
import com.messenger.ui.adapter.holder.chat.UserImageMessageViewHolder;
import com.messenger.ui.adapter.holder.chat.UserTextMessageViewHolder;
import com.messenger.ui.anim.SimpleAnimatorListener;
import com.messenger.util.ChatDateUtils;
import com.messenger.util.ChatTimestampFormatter;
import com.messenger.util.LinkHandlerMovementMethod;
import com.messenger.util.MessageVersionHelper;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.worldventures.dreamtrips.R;

public class MessagesCursorAdapter extends CursorRecyclerViewAdapter<MessageHolder> {
    private static final int VIEW_TYPE_OWN_TEXT_MESSAGE = 1;
    private static final int VIEW_TYPE_USER_TEXT_MESSAGE = 2;
    private static final int VIEW_TYPE_OWN_IMAGE_MESSAGE = 3;
    private static final int VIEW_TYPE_USER_IMAGE_MESSAGE = 4;

    private final DataUser user;
    private final String userLocale;
    private final ChatTimestampFormatter timestampFormatter;
    private DataConversation conversation;

    private OnAvatarClickListener avatarClickListener;
    private OnRepeatMessageSend onRepeatMessageSend;
    private OnMessageClickListener messageClickListener;
    private OnMessageLongClickListener messageLongClickListener;
    private OnImageClickListener onImageClickListener;

    private int manualTimestampPositionToAdd = -1;
    private int manualTimestampPosition = -1;
    private int manualTimestampPositionToRemove = -1;

    private boolean needMarkUnreadMessages;

    public MessagesCursorAdapter(@NonNull Context context, @NonNull DataUser user,
                                 @NonNull String userLocale, @Nullable Cursor cursor) {
        super(cursor);
        this.user = user;
        this.userLocale = userLocale;
        this.timestampFormatter = new ChatTimestampFormatter(context);
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_OWN_TEXT_MESSAGE:
                return new OwnTextMessageViewHolder(inflateRow(parent, R.layout.list_item_chat_own_text_messsage));
            case VIEW_TYPE_USER_TEXT_MESSAGE:
                return new UserTextMessageViewHolder(inflateRow(parent, R.layout.list_item_chat_user_text_message));
            case VIEW_TYPE_OWN_IMAGE_MESSAGE:
                return new OwnImageMessageViewHolder(inflateRow(parent, R.layout.list_item_chat_own_image_message));
            case VIEW_TYPE_USER_IMAGE_MESSAGE:
                return new UserImageMessageViewHolder(inflateRow(parent, R.layout.list_item_chat_user_image_message));
        }
        throw new IllegalArgumentException("No such view type in adapter");
    }

    @Override
    public void onBindViewHolderCursor(MessageHolder holder, Cursor cursor) {
        switch (getItemViewType(cursor.getPosition())) {
            case VIEW_TYPE_OWN_TEXT_MESSAGE:
                bindOwnTextMessageHolder((OwnTextMessageViewHolder) holder, cursor);
                break;
            case VIEW_TYPE_USER_TEXT_MESSAGE:
                bindUserTextMessageHolder((UserTextMessageViewHolder) holder, cursor);
                break;
            case VIEW_TYPE_OWN_IMAGE_MESSAGE:
                bindOwnImageMessageHolder((OwnImageMessageViewHolder) holder, cursor);
                break;
            case VIEW_TYPE_USER_IMAGE_MESSAGE:
                bindUserImageMessageHolder((UserImageMessageViewHolder) holder, cursor);
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // All Messages
    ///////////////////////////////////////////////////////////////////////////

    private void bindMessageHolder(MessageHolder holder, Cursor cursor) {
        int position = cursor.getPosition();
        DataMessage message = SqlUtils.convertToModel(true, DataMessage.class, cursor);
        holder.setMessage(message);
        holder.setPreviousMessageFromTheSameUser(previousMessageIsFromSameUser(cursor));
        bindTimeStampIfNeeded(holder, cursor, position, message);
        holder.setSelected(position == manualTimestampPosition);
        holder.setBubbleBackground();
        holder.updateMessageStatusUi(needMarkUnreadMessages);
        View.OnLongClickListener onLongClickListener = view -> {
            if (messageLongClickListener != null) {
                messageLongClickListener.onMessageLongClick(message);
            }
            return true;
        };
        holder.getMessageView().setOnLongClickListener(onLongClickListener);
        //TODO tweak for user message. fix it later
        if (holder instanceof UserTextMessageViewHolder) {
            ((UserTextMessageViewHolder) holder).getMessageTextView()
                    .setOnLongClickListener(onLongClickListener);
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // Text Messages
    ///////////////////////////////////////////////////////////////////////////

    private void bindOwnTextMessageHolder(OwnTextMessageViewHolder holder, Cursor cursor) {
        bindMessageHolder(holder, cursor);
        bindTextMessageHolder(holder, cursor);
        bindOwnUserMessageHolder(holder, cursor);
    }

    private void bindUserTextMessageHolder(UserTextMessageViewHolder holder, Cursor cursor) {
        boolean translationExist = !TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(MessageDAO.TRANSLATION_ID)));
        DataTranslation dataTranslation = translationExist? SqlUtils.convertToModel(true, DataTranslation.class, cursor) : null;
        holder.setTranslation(dataTranslation, userLocale);
        //
        bindMessageHolder(holder, cursor);
        bindTextMessageHolder(holder, cursor);
        bindUserMessageHolder(holder, cursor);
    }

    private void bindTextMessageHolder(TextMessageViewHolder holder, Cursor cursor) {
        int version = cursor.getInt(cursor.getColumnIndex(DataMessage$Table.VERSION));
        String attachmentType = cursor.getString(cursor.getColumnIndex(DataAttachment$Table.TYPE));

        if (MessageVersionHelper.isUnsupported(version, attachmentType))
            holder.showUnsupportMessage();
        else holder.showMessage();

        holder.getMessageTextView().setMovementMethod(LinkHandlerMovementMethod.getInstance());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Image Messages
    ///////////////////////////////////////////////////////////////////////////

    public void bindOwnImageMessageHolder(OwnImageMessageViewHolder holder, Cursor cursor) {
        bindMessageHolder(holder, cursor);
        bindOwnUserMessageHolder(holder, cursor);
        bindImageMessageHolder(holder, cursor);
    }

    public void bindUserImageMessageHolder(UserImageMessageViewHolder holder, Cursor cursor) {
        bindMessageHolder(holder, cursor);
        bindUserMessageHolder(holder, cursor);
        bindImageMessageHolder(holder, cursor);
    }

    public void bindImageMessageHolder(ImageMessageViewHolder holder, Cursor cursor) {
        String attachmentImageId = cursor.getString(cursor.getColumnIndex(MessageDAO.ATTACHMENT_ID));
        String imageUrl = cursor.getString(cursor.getColumnIndex(DataAttachment$Table.URL));

        holder.showImageMessage(TextUtils.isEmpty(imageUrl) ? Uri.EMPTY : Uri.parse(imageUrl));
        holder.setOnImageClickListener(v -> {
            if (onImageClickListener != null)
                onImageClickListener.onImageClickListener(attachmentImageId);
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Own and Someones messages
    ///////////////////////////////////////////////////////////////////////////

    public void bindOwnUserMessageHolder(MessageHolder.OwnMessageHolder holder, Cursor cursor) {
        holder.setOnRepeatMessageListener(onRepeatMessageSend);
    }

    public void bindUserMessageHolder(MessageHolder.UserMessageHolder holder, Cursor cursor) {
        DataUser userFrom = SqlUtils.convertToModel(true, DataUser.class, cursor);

        holder.setAuthor(userFrom);
        holder.setAvatarClickListener(avatarClickListener);

        holder.updateAvatar();
        holder.updateName(conversation);
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        boolean ownMessage = cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID))
                .equals(user.getId());
        String attachmentType = cursor.getString(cursor.getColumnIndex(DataAttachment$Table.TYPE));
        boolean imageMessage = AttachmentType.IMAGE.equals(attachmentType);
        if (!imageMessage) {
            return ownMessage ? VIEW_TYPE_OWN_TEXT_MESSAGE : VIEW_TYPE_USER_TEXT_MESSAGE;
        } else {
            return ownMessage ? VIEW_TYPE_OWN_IMAGE_MESSAGE : VIEW_TYPE_USER_IMAGE_MESSAGE;
        }
    }

    public void setConversation(DataConversation conversation) {
        this.conversation = conversation;
    }

    public void setNeedMarkUnreadMessages(boolean needMarkUnreadMessages) {
        this.needMarkUnreadMessages = needMarkUnreadMessages;
    }

    public void setAvatarClickListener(@Nullable OnAvatarClickListener avatarClickListener) {
        this.avatarClickListener = avatarClickListener;
    }

    public void setOnRepeatMessageSend(OnRepeatMessageSend onRepeatMessageSend) {
        this.onRepeatMessageSend = onRepeatMessageSend;
    }

    public void setMessageClickListener(OnMessageClickListener messageClickListener) {
        this.messageClickListener = messageClickListener;
    }

    public void setMessageLongClickListener(OnMessageLongClickListener messageLongClickListener) {
        this.messageLongClickListener = messageLongClickListener;
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Util and helpers
    ///////////////////////////////////////////////////////////////////////////

    private View inflateRow(ViewGroup parent, int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    private boolean previousMessageIsFromSameUser(Cursor cursor) {
        String currentId = cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID));
        boolean moveCursorToPrev = cursor.moveToPrevious();
        if (!moveCursorToPrev) {
            cursor.moveToNext();
            return false;
        }
        String prevId = cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID));
        cursor.moveToNext();
        return prevId.equals(currentId);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Message timestamps
    ///////////////////////////////////////////////////////////////////////////

    private void showManualTimestampForPosition(int position) {
        if (position == manualTimestampPosition) {
            manualTimestampPositionToRemove = manualTimestampPosition;
            notifyItemChanged(position);
        } else {
            int prevPosition = manualTimestampPosition;
            manualTimestampPositionToAdd = position;
            manualTimestampPositionToRemove = prevPosition;
            notifyItemChanged(prevPosition);
            notifyItemChanged(manualTimestampPositionToAdd);
        }
    }

    private void bindTimeStampIfNeeded(MessageHolder holder, Cursor cursor, int position, DataMessage message) {
        boolean manualTimestamp = manualTimestampPosition == position
                || manualTimestampPositionToAdd == position
                || manualTimestampPositionToRemove == position;
        String dateDivider;
        if (manualTimestamp) {
            dateDivider = timestampFormatter.getMessageDateManualTimestamp(cursor
                    .getLong(cursor.getColumnIndex(DataMessage$Table.DATE)));
        } else {
            dateDivider = getMessageTimestampBetweenMessagesIfNeeded(cursor);
        }
        int messageStatus = cursor.getInt(cursor.getColumnIndex(DataMessage$Table.STATUS));

        boolean clickableTimestamp = messageStatus != MessageStatus.ERROR && (manualTimestamp || TextUtils.isEmpty(dateDivider));

        if (messageStatus == MessageStatus.ERROR) {
            holder.dateTextView.setVisibility(View.GONE);
            return;
        }

        View.OnClickListener listener = view -> {
            if (messageClickListener != null) {
                messageClickListener.onMessageClick(message);
            }
            if (clickableTimestamp) {
                showManualTimestampForPosition(position);
            }
        };

        holder.getMessageView().setOnClickListener(listener);
        //TODO tweak for user message. fix it later
        if (holder instanceof UserTextMessageViewHolder) {
            ((UserTextMessageViewHolder) holder).getMessageTextView().setOnClickListener(listener);
        }

        TextView dateTextView = holder.dateTextView;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dateTextView.getLayoutParams();
        if (manualTimestampPositionToAdd == position) {
            manualTimestampPositionToAdd = -1;
            manualTimestampPosition = position;

            holder.dateTextView.setVisibility(View.VISIBLE);
            dateTextView.setText(dateDivider);

            if (dateTextView.getMeasuredHeight() == 0) {
                dateTextView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            }
            ValueAnimator animator = ValueAnimator.ofFloat(-dateTextView.getMeasuredHeight(), 0);
            animator.addUpdateListener(valueAnimator -> {
                float margin = (Float) valueAnimator.getAnimatedValue();
                params.bottomMargin = (int) margin;
                dateTextView.requestLayout();
            });
            animator.start();
        } else if (manualTimestampPositionToRemove == position) {
            manualTimestampPositionToRemove = -1;
            manualTimestampPosition = -1;
            ValueAnimator animator = ValueAnimator.ofFloat(0, -dateTextView.getMeasuredHeight());
            animator.addUpdateListener(valueAnimator -> {
                float margin = (Float) valueAnimator.getAnimatedValue();
                params.bottomMargin = (int) margin;
                dateTextView.requestLayout();
            });
            animator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    dateTextView.setVisibility(View.GONE);
                }
            });
            animator.start();
        } else {
            params.bottomMargin = 0;
            if (!TextUtils.isEmpty(dateDivider)) {
                dateTextView.setVisibility(View.VISIBLE);
                dateTextView.setText(dateDivider);
            } else {
                dateTextView.setVisibility(View.GONE);
            }
        }
    }

    private String getMessageTimestampBetweenMessagesIfNeeded(Cursor cursor) {
        int dateColumnIndex = cursor.getColumnIndex(DataMessage$Table.DATE);
        long currentDate = cursor.getLong(dateColumnIndex);
        long previousDate = 0;
        boolean moveCursorToPrev = cursor.moveToPrevious();
        if (moveCursorToPrev) {
            previousDate = cursor.getLong(dateColumnIndex);
        }
        cursor.moveToNext();
        return getMessageTimestampBetweenDatesIfNeeded(previousDate, currentDate);
    }

    private String getMessageTimestampBetweenDatesIfNeeded(long previousDate, long currentDate) {
        int calendarDaysSincePreviousDate = 0;
        if (previousDate != 0) {
            calendarDaysSincePreviousDate = (int) ChatDateUtils
                    .calendarDaysBetweenDates(previousDate, currentDate);
        }
        if ((previousDate != 0 && calendarDaysSincePreviousDate > 0) || previousDate == 0) {
            return timestampFormatter.getMessageDateDividerTimestamp(currentDate);
        }
        return null;
    }

    ///////////////////////////////////////////
    ////// Listeners
    ///////////////////////////////////////////

    public interface OnAvatarClickListener {
        void onAvatarClick(DataUser user);
    }

    public interface OnRepeatMessageSend {
        void onRepeatMessageSend(String messageId);
    }

    public interface OnMessageClickListener {
        void onMessageClick(DataMessage message);
    }

    public interface OnMessageLongClickListener {
        void onMessageLongClick(DataMessage message);
    }

    public interface OnImageClickListener {
        void onImageClickListener(String attachmentImageId);
    }

}

package com.messenger.ui.adapter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage$Table;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.ui.adapter.holder.chat.ChatViewHolderProvider;
import com.messenger.ui.adapter.holder.chat.MessageViewHolder;
import com.messenger.ui.anim.SimpleAnimatorListener;
import com.messenger.util.ChatDateUtils;
import com.messenger.util.ChatTimestampFormatter;

import javax.inject.Inject;

public class ChatAdapter extends CursorRecyclerViewAdapter<MessageViewHolder> {

    private int manualTimestampPositionToAdd = -1;
    private int manualTimestampPosition = -1;
    private int manualTimestampPositionToRemove = -1;

    private boolean needMarkUnreadMessages;
    private DataConversation dataConversation;

    @Inject
    ChatViewHolderProvider viewHolderProvider;
    @Inject
    ChatTimestampFormatter timestampFormatter;

    private ChatCellDelegate cellDelegate;

    public ChatAdapter(Cursor cursor) {
        super(cursor);
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
        bindTimeStampIfNeeded(holder, cursor, position);
        holder.setSelected(position == manualTimestampPosition);
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

    private void bindTimeStampIfNeeded(MessageViewHolder holder, Cursor cursor, int position) {
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
            if (clickableTimestamp) {
                showManualTimestampForPosition(position);
            }
        };

        holder.getMessageView().setOnClickListener(listener);

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

}

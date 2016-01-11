package com.messenger.ui.adapter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.Message$Table;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.holder.MessageHolder;
import com.messenger.ui.adapter.holder.OwnMessageViewHolder;
import com.messenger.ui.adapter.holder.UserMessageViewHolder;
import com.messenger.ui.anim.SimpleAnimatorListener;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.util.ChatDateUtils;
import com.messenger.util.Constants;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessagesCursorAdapter extends CursorRecyclerViewAdapter<MessageHolder> {
    private static final int VIEW_TYPE_OWN_MESSAGE = 1;
    private static final int VIEW_TYPE_SOMEONES_MESSAGE = 2;

    private final User user;
    private final Context context;
    private final ConversationHelper conversationHelper;
    private Conversation conversation;

    private OnAvatarClickListener avatarClickListener;
    private OnRepeatMessageSend onRepeatMessageSend;
    private OnMessageClickListener messageClickListener;

    private SimpleDateFormat timeDateFormatter;
    private SimpleDateFormat dayOfTheWeekDateFormatter;
    private SimpleDateFormat dayOfTheMonthDateFormatter;

    private final int rowVerticalMargin;

    private int manualTimestampPositionToAdd = -1;
    private int manualTimestampPosition = -1;
    private int manualTimestampPositionToRemove = -1;

    public interface OnAvatarClickListener {
        void onAvatarClick(User user);
    }

    public interface OnRepeatMessageSend {
        void onRepeatMessageSend(String messageId);
    }

    public interface OnMessageClickListener {
        void onMessageClick(Message message);
    }

    public MessagesCursorAdapter(@NonNull Context context, @NonNull User user, @Nullable Cursor cursor) {
        super(cursor);
        this.context = context;
        this.user = user;

        this.conversationHelper = new ConversationHelper();

        this.timeDateFormatter = new SimpleDateFormat("h:mm aa");
        this.dayOfTheWeekDateFormatter = new SimpleDateFormat("EEEE");
        this.dayOfTheMonthDateFormatter = new SimpleDateFormat("MMM dd");

        rowVerticalMargin = context.getResources()
                .getDimensionPixelSize(R.dimen.chat_list_item_vertical_padding);
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_OWN_MESSAGE: {
                View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_own_messsage,
                        parent, false);
                return new OwnMessageViewHolder(itemRow);
            }
            case VIEW_TYPE_SOMEONES_MESSAGE: {
                View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_someones_message,
                        parent, false);
                return new UserMessageViewHolder(itemRow);
            }
        }
        throw new IllegalArgumentException("No such view type in adapter");
    }

    @Override
    public void onBindViewHolderCursor(MessageHolder holder, Cursor cursor) {
        switch (getItemViewType(cursor.getPosition())) {
            case VIEW_TYPE_OWN_MESSAGE:
                bindOwnMessageHolder((OwnMessageViewHolder) holder, cursor);
                break;
            case VIEW_TYPE_SOMEONES_MESSAGE:
                bindUserMessageHolder((UserMessageViewHolder) holder, cursor);
                break;
        }
    }

    private void bindMessageHolder(MessageHolder holder, int position, Cursor cursor, Message message) {
        boolean manualTimestamp = manualTimestampPosition == position
                || manualTimestampPositionToAdd == position
                || manualTimestampPositionToRemove == position;
        String dateDivider;
        if (manualTimestamp) {
            dateDivider = getDateTimestamp(cursor.getLong(cursor.getColumnIndex(Message$Table.DATE)));
        } else {
            dateDivider = getMessageTimestampBetweenMessagesIfNeeded(cursor);
        }
        boolean clickableTimestamp = manualTimestamp || TextUtils.isEmpty(dateDivider);
        holder.messageTextView
                .setOnTouchListener(new TextSelectableClickListener(context, cursor.getPosition(),
                        message, clickableTimestamp));

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
        } else if (manualTimestampPositionToRemove == position){
            manualTimestampPositionToRemove = -1;
            manualTimestampPosition = -1;
            ValueAnimator animator = ValueAnimator.ofFloat(0, -dateTextView.getMeasuredHeight());
            animator.addUpdateListener(valueAnimator -> {
                float margin = (Float)valueAnimator.getAnimatedValue();
                params.bottomMargin = (int)margin;
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

    private class TextSelectableClickListener
            implements View.OnTouchListener, GestureDetector.OnGestureListener {

        Message message;
        int position;
        GestureDetectorCompat gestureDetector;
        boolean clickableTimestamp;

        public TextSelectableClickListener(Context context, int position, Message message,
                                           boolean clickableTimestamp) {
            this.gestureDetector = new GestureDetectorCompat(context, this);
            this.position = position;
            this.message = message;
            this.clickableTimestamp = clickableTimestamp;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            if (clickableTimestamp) {
                showManualTimestampForPosition(position);
            }
            if (messageClickListener != null) {
                messageClickListener.onMessageClick(message);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    }

    private String getMessageTimestampBetweenMessagesIfNeeded(Cursor cursor) {
        int dateColumnIndex = cursor.getColumnIndex(Message$Table.DATE);
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
            return getDateTimestamp(currentDate);
        }
        return null;
    }

    @NonNull
    private String getDateTimestamp(long currentDate) {
        StringBuilder sb = new StringBuilder();
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTimeInMillis(currentDate);
        Calendar todayMidnightCalendar = ChatDateUtils.getToday();
        long todayMidnightTimestamp = todayMidnightCalendar.getTime().getTime();
        int daysSinceToday = (int) ChatDateUtils
                .calendarDaysBetweenDates(todayMidnightTimestamp, currentDate);
        if (daysSinceToday == 0) {
            sb.append(context.getString(R.string.chat_list_date_entry_today));
            sb.append(", ");
            sb.append(timeDateFormatter.format(currentDate));
        } else if (daysSinceToday == 1) {
            sb.append(context.getString(R.string.chat_list_date_entry_yesterday));
            sb.append(", ");
            sb.append(timeDateFormatter.format(currentDate));
        } else if (daysSinceToday > 1 && daysSinceToday < 7) {
            sb.append(dayOfTheWeekDateFormatter.format(currentDate));
            sb.append(", ");
            sb.append(timeDateFormatter.format(currentDate));
        } else if (dateCalendar.get(Calendar.YEAR) == todayMidnightCalendar.get(Calendar.YEAR)) {
            sb.append(dayOfTheMonthDateFormatter.format(currentDate));
            sb.append(", ");
            sb.append(timeDateFormatter.format(currentDate));
        } else {
            sb.append(DateUtils.getRelativeDateTimeString(context, currentDate,
                    DateUtils.HOUR_IN_MILLIS, DateUtils.YEAR_IN_MILLIS, 0));
        }
        return sb.toString();
    }

    private boolean previousMessageIsFromSameUser(Cursor cursor) {
        String currentId = cursor.getString(cursor.getColumnIndex(Message$Table.FROMID));
        boolean moveCursorToPrev = cursor.moveToPrevious();
        if (!moveCursorToPrev) {
            return false;
        }
        String prevId = cursor.getString(cursor.getColumnIndex(Message$Table.FROMID));
        cursor.moveToNext();
        return prevId.equals(currentId);
    }

    private void bindOwnMessageHolder(OwnMessageViewHolder holder, Cursor cursor) {
        holder.setMessageId(cursor.getString(cursor.getColumnIndex(Message$Table._ID)));
        holder.setOnRepeatMessageSend(onRepeatMessageSend);
        holder.visibleError(cursor.getInt(cursor.getColumnIndex(Message$Table.STATUS)) == Message.Status.ERROR);
        holder.messageTextView.setText(cursor.getString(cursor.getColumnIndex(Message$Table.TEXT)));

        int position = cursor.getPosition();
        Message message = SqlUtils.convertToModel(true, Message.class, cursor);

        bindMessageHolder(holder, position, cursor, message);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder
                .chatMessageContainer.getLayoutParams();
        int backgroundResource;
        // disabled until dark blue bubbles assets are provided
        boolean selectedMessage = false;
        //boolean selectedMessage = position == manualTimestampPosition;
        if (previousMessageIsFromSameUser(cursor)) {
            params.setMargins(params.leftMargin, 0, params.rightMargin, rowVerticalMargin);
            //backgroundResource = selectedMessage? R.drawable.dark_blue_bubble: R.drawable.blue_bubble;
            backgroundResource = R.drawable.blue_bubble;
        } else {
            params.setMargins(params.leftMargin, rowVerticalMargin, params.rightMargin, rowVerticalMargin);
            //backgroundResource = selectedMessage? R.drawable.dark_blue_bubble_comics: R.drawable.blue_bubble_comics;
            backgroundResource = R.drawable.blue_bubble_comics;
        }
        holder.messageTextView.setBackgroundResource(backgroundResource);
    }

    private void bindUserMessageHolder(UserMessageViewHolder holder, Cursor cursor) {
        int position = cursor.getPosition();
        Message message = SqlUtils.convertToModel(true, Message.class, cursor);
        User userFrom = SqlUtils.convertToModel(true, User.class, cursor);

        bindMessageHolder(holder, position, cursor, message);

        boolean isPreviousMessageFromTheSameUser = previousMessageIsFromSameUser(cursor);
        if (conversationHelper.isGroup(conversation) && userFrom != null
                && !isPreviousMessageFromTheSameUser) {
            holder.nameTextView.setVisibility(View.VISIBLE);
            holder.nameTextView.setText(userFrom.getName());
        } else {
            holder.nameTextView.setVisibility(View.GONE);
        }

        holder.messageTextView.setText(message.getText());

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder
                .chatMessageContainer.getLayoutParams();
        int backgroundResource;
        // disabled until dark blue bubbles assets are provided
        boolean selectedMessage = false;
        //boolean selectedMessage = position == manualTimestampPosition;
        if (isPreviousMessageFromTheSameUser) {
            params.setMargins(params.leftMargin, 0, params.rightMargin, rowVerticalMargin);
            holder.avatarImageView.setVisibility(View.INVISIBLE);
            holder.messageTextView.setBackgroundResource(R.drawable.grey_bubble);
            backgroundResource = selectedMessage ? R.drawable.dark_grey_bubble
                    : R.drawable.grey_bubble;
        } else {
            params.setMargins(params.leftMargin, rowVerticalMargin, params.rightMargin, rowVerticalMargin);
            backgroundResource = selectedMessage ? R.drawable.dark_grey_bubble_comics
                    : R.drawable.grey_bubble_comics;
            holder.avatarImageView.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(userFrom == null ? null : userFrom.getAvatarUrl())
                    .placeholder(Constants.PLACEHOLDER_USER_AVATAR_SMALL)
                    .into(holder.avatarImageView);

            holder.avatarImageView.setOnClickListener(v -> {
                if (avatarClickListener != null) {
                    avatarClickListener.onAvatarClick(userFrom);
                }
            });
        }
        holder.messageTextView.setBackgroundResource(backgroundResource);
    }

    public void showManualTimestampForPosition(int position) {
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

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndex(Message$Table.FROMID))
                .equals(user.getId()) ? VIEW_TYPE_OWN_MESSAGE : VIEW_TYPE_SOMEONES_MESSAGE;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
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
}

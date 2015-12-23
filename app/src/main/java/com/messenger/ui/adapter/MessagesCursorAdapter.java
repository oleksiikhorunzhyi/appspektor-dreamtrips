package com.messenger.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.holder.MessageHolder;
import com.messenger.ui.adapter.holder.OwnMessageViewHolder;
import com.messenger.ui.adapter.holder.UserMessageViewHolder;
import com.messenger.ui.adapter.holder.ViewHolder;
import com.messenger.util.ChatDateFormatter;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessagesCursorAdapter extends CursorRecyclerViewAdapter<MessageHolder> {
    private static final int VIEW_TYPE_OWN_MESSAGE = 1;
    private static final int VIEW_TYPE_SOMEONES_MESSAGE = 2;

    private final User user;
    private final Context context;
    private Conversation conversation;
    private SimpleDateFormat timeDateFormatter;
    private SimpleDateFormat dayOfTheWeekDateFormatter;
    private SimpleDateFormat dayOfTheMonthDateFormatter;

    private final int rowVerticalMargin;

    public MessagesCursorAdapter(@NonNull Context context, @NonNull User user, @Nullable Cursor cursor) {
        super(cursor);
        this.context = context;
        this.user = user;

        this.timeDateFormatter = new SimpleDateFormat("h:mm");
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
                bindMessageHolder(holder, cursor);
                bindOwnMessageHolder((OwnMessageViewHolder) holder, cursor);
                break;
            case VIEW_TYPE_SOMEONES_MESSAGE:
                bindMessageHolder(holder, cursor);
                bindUserMessageHolder((UserMessageViewHolder) holder, cursor);
                break;
        }
    }

    private void bindMessageHolder(MessageHolder holder, Cursor cursor) {
        String dateDivider = getDateDivider(cursor);
        if (!TextUtils.isEmpty(dateDivider)) {
            holder.dateTextView.setVisibility(View.VISIBLE);
            holder.dateTextView.setText(dateDivider);
        } else {
            holder.dateTextView.setVisibility(View.GONE);
        }
    }

    private void bindOwnMessageHolder(OwnMessageViewHolder holder, Cursor cursor) {
        holder.messageTextView.setText(cursor.getString(cursor.getColumnIndex(Message.COLUMN_TEXT)));

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (previousMessageIsFromSameUser(cursor)) {
            params.setMargins(params.leftMargin, rowVerticalMargin, params.rightMargin, rowVerticalMargin);
            holder.messageTextView.setBackgroundResource(R.drawable.blue_bubble);
        } else {
            params.setMargins(params.leftMargin, 0, params.rightMargin, 0);
            holder.messageTextView.setBackgroundResource(R.drawable.blue_bubble_comics);
        }
    }

    private String getDateDivider(Cursor cursor) {
        int dateColumnIndex = cursor.getColumnIndex(Message.COLUMN_DATE);
        long currentDate = cursor.getLong(dateColumnIndex);
        long previousDate = 0;
        Log.d("NewChat", "Current pos " + cursor.getPosition());
        boolean moveCursorToPrev = cursor.moveToPrevious();
        Log.d("NewChat", "Pos after move " + cursor.getPosition() + " " + moveCursorToPrev);
        if (moveCursorToPrev) {
            previousDate = cursor.getLong(dateColumnIndex);
            Log.d("NewChat", "Pos after restore " + cursor.getPosition());
        }
        cursor.moveToNext();
        Log.d("NewChat", "Final pos " + cursor.getPosition());
        return getDateEntryIfNeeded(previousDate, currentDate);
    }

    public String getDateEntryIfNeeded(long previousDate, long currentDate) {
        StringBuilder dateString = new StringBuilder();
        int calendarDaysSincePreviousDate = 0;
        if (previousDate != 0) {
            calendarDaysSincePreviousDate = (int) ChatDateFormatter.calendarDaysBetweenDates(previousDate, currentDate);
        }
        if ((previousDate != 0 && calendarDaysSincePreviousDate > 0) || previousDate == 0) {
            int daysSinceToday = (int) ChatDateFormatter.calendarDaysBetweenDates(ChatDateFormatter.getToday()
                    .getTime().getTime(), currentDate);
            if (daysSinceToday == 0) {
                dateString.append(context.getString(R.string.chat_list_date_entry_today));
                dateString.append(", ");
                dateString.append(timeDateFormatter.format(currentDate));
            } else if (daysSinceToday == 1) {
                dateString.append(context.getString(R.string.chat_list_date_entry_yesterday));
                dateString.append(", ");
                dateString.append(timeDateFormatter.format(currentDate));
            } else if (daysSinceToday > 1 && daysSinceToday <= 4) {
                dateString.append(dayOfTheWeekDateFormatter.format(currentDate).toUpperCase());
                dateString.append(", ");
                dateString.append(timeDateFormatter.format(currentDate));
            } else {
                dateString.append(dayOfTheMonthDateFormatter.format(currentDate).toUpperCase());
                dateString.append(", ");
                dateString.append(timeDateFormatter.format(currentDate));
            }
        }
        return dateString.toString();
    }

    private boolean previousMessageIsFromSameUser(Cursor cursor) {
        String currentId = cursor.getString(cursor.getColumnIndex(Message.COLUMN_FROM));
        boolean moveCursorToPrev = cursor.moveToPrevious();
        if (!moveCursorToPrev) {
            return false;
        }
        String prevId = cursor.getString(cursor.getColumnIndex(Message.COLUMN_FROM));
        cursor.moveToNext();
        return prevId.equals(currentId);
    }

    private void bindUserMessageHolder(UserMessageViewHolder holder, Cursor cursor) {
        Message message = SqlUtils.convertToModel(true, Message.class, cursor);
        User userFrom = SqlUtils.convertToModel(true, User.class, cursor);

        if (isGroupConversation(conversation.getType()) && userFrom != null) {
            holder.nameTextView.setVisibility(View.VISIBLE);
            holder.nameTextView.setText(userFrom.getName());
        } else {
            holder.nameTextView.setVisibility(View.GONE);
        }

        holder.messageTextView.setText(message.getText());

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (previousMessageIsFromSameUser(cursor)) {
            params.setMargins(params.leftMargin, 0, params.rightMargin, 0);
            holder.avatarImageView.setVisibility(View.INVISIBLE);
            holder.messageTextView.setBackgroundResource(R.drawable.grey_bubble);
        } else {
            params.setMargins(params.leftMargin, rowVerticalMargin, params.rightMargin, rowVerticalMargin);
            holder.messageTextView.setBackgroundResource(R.drawable.grey_bubble_comics);
            holder.avatarImageView.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(userFrom == null ? null : userFrom.getAvatarUrl())
                    .placeholder(android.R.drawable.ic_menu_compass)
                    .into(holder.avatarImageView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndex(Message.COLUMN_FROM))
                .equals(user.getId()) ? VIEW_TYPE_OWN_MESSAGE : VIEW_TYPE_SOMEONES_MESSAGE;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    private boolean isGroupConversation(String conversationType) {
        return !conversationType.equalsIgnoreCase(Conversation.Type.CHAT);
    }
}

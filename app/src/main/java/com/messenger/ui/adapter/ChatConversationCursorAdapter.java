package com.messenger.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.holders.OwnMessageViewHolder;
import com.messenger.ui.adapter.holders.UserMessageViewHolder;
import com.messenger.ui.adapter.holders.ViewHolder;
import com.messenger.util.ChatDateFormatter;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatConversationCursorAdapter extends CursorRecyclerViewAdapter<ViewHolder> {
    private static final int VIEW_TYPE_OWN_MESSAGE = 1;
    private static final int VIEW_TYPE_OWN_MESSAGE_WITH_DATE = VIEW_TYPE_OWN_MESSAGE << 1;
    private static final int VIEW_TYPE_SOMEONES_MESSAGE = 11;
    private static final int VIEW_TYPE_SOMEONES_MESSAGE_WITH_DATE = VIEW_TYPE_SOMEONES_MESSAGE << 1;

    private final User user;
    private final Context context;
    private final SimpleDateFormat todayDateEntryFormatter;
    private final SimpleDateFormat yesterdayDateEntryFormatter;
    private final SimpleDateFormat fewDaysAgoDateEntryFormatter;
    private final SimpleDateFormat manyDaysAgoDateEntryFormatter;

    private final int rowVerticalMargin;

    public ChatConversationCursorAdapter(@NonNull Context context, @NonNull User user, @Nullable Cursor cursor) {
        super(cursor);
        this.context = context;
        this.user = user;
        rowVerticalMargin = context.getResources().getDimensionPixelSize(R.dimen.chat_list_item_vertical_padding);
        this.todayDateEntryFormatter = new SimpleDateFormat(context
                .getString(R.string.chat_list_date_entry_today_format));
        this.yesterdayDateEntryFormatter = new SimpleDateFormat(context
                .getString(R.string.chat_list_date_entry_yesterday_format));
        this.fewDaysAgoDateEntryFormatter = new SimpleDateFormat(context
                .getString(R.string.chat_list_date_entry_few_days_ago_format));
        this.manyDaysAgoDateEntryFormatter = new SimpleDateFormat(context
                .getString(R.string.chat_list_date_entry_many_days_ago_format));
    }


    @Override
    public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
        switch (getItemViewType(cursor.getPosition())) {
            case VIEW_TYPE_OWN_MESSAGE_WITH_DATE:
            case VIEW_TYPE_OWN_MESSAGE:
                bindOwnMessageHolder((OwnMessageViewHolder) holder, cursor);
                break;
            case VIEW_TYPE_SOMEONES_MESSAGE_WITH_DATE:
            case VIEW_TYPE_SOMEONES_MESSAGE:
                bindUserMessageHolder((UserMessageViewHolder) holder, cursor);
                break;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_OWN_MESSAGE_WITH_DATE:
            case VIEW_TYPE_OWN_MESSAGE: {
                View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_own_messsage,
                        parent, false);
                return new OwnMessageViewHolder(itemRow);
            }
            case VIEW_TYPE_SOMEONES_MESSAGE_WITH_DATE:
            case VIEW_TYPE_SOMEONES_MESSAGE: {
                View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_someones_message,
                        parent, false);
                return new UserMessageViewHolder(itemRow);
            }
        }
        throw new IllegalArgumentException("No such view type in adapter");
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

    private boolean previousMessageIsFromSameUser(Cursor cursor) {
        final int position = cursor.getPosition();
        final boolean result = !cursor.moveToPrevious() || cursor.getString(cursor.getColumnIndex(Message.COLUMN_FROM)) != user.getId();
        cursor.moveToPosition(position);
        return result;
    }

    private void bindUserMessageHolder(UserMessageViewHolder holder, Cursor cursor) {
        Message message = SqlUtils.convertToModel(true, Message.class, cursor);
        // TODO: 12/14/15 chatConversation.isGroupConversation()
        if (false) {
            holder.nameTextView.setVisibility(View.VISIBLE);
            holder.nameTextView.setText(message.getFrom().getName());
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
            Picasso.with(context).load(message.getFrom()
                    .getAvatarUrl()).placeholder(android.R.drawable.ic_menu_compass)
                    .into(holder.avatarImageView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        final int messageType = cursor.getString(cursor.getColumnIndex(Message.COLUMN_FROM))
                .equals(user.getId()) ? VIEW_TYPE_OWN_MESSAGE : VIEW_TYPE_SOMEONES_MESSAGE;

        final int timeColumn = cursor.getColumnIndex(Message.COLUMN_DATE);
        final long currentDate = cursor.getLong(timeColumn);
        int result = (!cursor.moveToPrevious() || ChatDateFormatter.calendarDaysBetweenDates(currentDate, cursor.getLong(timeColumn)) > 0)
                ? messageType << 1 : messageType;
        cursor.moveToPosition(position);
        return result;
    }

    private String convertTime(long currentDate, long prevDate) {
        String dateString = null;
        int daysSinceToday = (int) ChatDateFormatter.calendarDaysBetweenDates(currentDate, prevDate);
        if (daysSinceToday == 0) {
            dateString = todayDateEntryFormatter.format(new Date(currentDate));
        } else if (daysSinceToday == 1) {
            dateString = yesterdayDateEntryFormatter.format(new Date(currentDate));
        } else if (daysSinceToday > 1 && daysSinceToday <= 4) {
            dateString = fewDaysAgoDateEntryFormatter.format(new Date(currentDate));
        } else {
            dateString = manyDaysAgoDateEntryFormatter.format(new Date(currentDate));
        }
        return dateString;
    }
}

package com.messenger.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;
import com.messenger.model.ChatConversation;
import com.messenger.model.ChatMessage;
import com.messenger.util.ChatDateFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import butterknife.ButterKnife;

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ViewHolder> {

    // Use this parameter and calculate margins needed for a message to not take up the whole row space
    // dynamically instead of using weights with LinearLayout in item rows or hardcoding margins in dimens.
    private static final float MESSAGE_SCREEN_WIDTH_SHARE = 0.6f;

    private static final boolean DEBUG_PRINT_DATES_FOR_EACH_MESSAGE = false;

    private static final int VIEW_TYPE_OWN_MESSAGE = 0;
    private static final int VIEW_TYPE_SOMEONES_MESSAGE = 1;
    private static final int VIEW_TYPE_DATE_ROW = 2;

    private Context context;

    private ChatConversation chatConversation;
    private List<Entry> entries = new ArrayList<>();

    private SimpleDateFormat todayDateEntryFormatter;
    private SimpleDateFormat yesterdayDateEntryFormatter;
    private SimpleDateFormat fewDaysAgoDateEntryFormatter;
    private SimpleDateFormat manyDaysAgoDateEntryFormatter;

    // Use this variable as margins that determine free space left in row after message and avatar took up
    // the space needed.
    private static int freeSpaceForMessageRowOwnMessage;
    private static int freeSpaceForMessageRowUserMessage;

    int rowVerticalMargin;

    private static class Entry {
        int viewType;
        ChatMessage message;
        boolean previousEntryIsMessageFromSameUser;
        String dateRowTitle;

        static Entry newOwnMessageEntry(ChatMessage message) {
            Entry entry = new Entry();
            entry.viewType = VIEW_TYPE_OWN_MESSAGE;
            entry.message = message;
            return entry;
        }

        static Entry newUserMessageEntry(ChatMessage message) {
            Entry entry = new Entry();
            entry.viewType = VIEW_TYPE_SOMEONES_MESSAGE;
            entry.message = message;
            return entry;
        }

        static Entry newDateEntry(String dateRowTitle) {
            Entry entry = new Entry();
            entry.viewType = VIEW_TYPE_DATE_ROW;
            entry.dateRowTitle = dateRowTitle;
            return entry;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.inject(this, itemView);
        }
    }

    public static class DateViewHolder extends ViewHolder {
        @InjectView(R.id.chat_date) TextView dateTextView;

        public DateViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class OwnMessageViewHolder extends ViewHolder {
        @InjectView(R.id.chat_message) TextView messageTextView;

        public OwnMessageViewHolder(View itemView) {
            super(itemView);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)messageTextView.getLayoutParams();
            params.setMargins(freeSpaceForMessageRowOwnMessage, params.topMargin, params.rightMargin,
                    params.bottomMargin);
        }
    }

    public static class UserMessageViewHolder extends ViewHolder {

        @InjectView(R.id.chat_item_avatar) ImageView avatarImageView;
        @InjectView(R.id.chat_username) TextView nameTextView;
        @InjectView(R.id.chat_message) TextView messageTextView;

        public UserMessageViewHolder(View itemView) {
            super(itemView);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)messageTextView.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, freeSpaceForMessageRowUserMessage,
                    params.bottomMargin);
        }
    }

    public ChatConversationAdapter(Context context) {
        this.context = context;
        rowVerticalMargin = context.getResources().getDimensionPixelSize(R.dimen.chat_list_item_vertical_padding);

        this.todayDateEntryFormatter = new SimpleDateFormat(context
                .getString(R.string.chat_list_date_entry_today_format));
        this.yesterdayDateEntryFormatter = new SimpleDateFormat(context
                .getString(R.string.chat_list_date_entry_yesterday_format));
        this.fewDaysAgoDateEntryFormatter = new SimpleDateFormat(context
                .getString(R.string.chat_list_date_entry_few_days_ago_format));
        this.manyDaysAgoDateEntryFormatter = new SimpleDateFormat(context
                .getString(R.string.chat_list_date_entry_many_days_ago_format));

        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int messageWidth = (int)(screenWidth * MESSAGE_SCREEN_WIDTH_SHARE);
        int ownMessageWidth = 2 * getDimen(R.dimen.chat_list_item_horizontal_padding)
                + messageWidth;
        freeSpaceForMessageRowOwnMessage = screenWidth - ownMessageWidth;
        int userMessageWidth = ownMessageWidth + getDimen(R.dimen.chat_list_item_horizontal_padding)
                + getDimen(R.dimen.list_item_small_avatar_image_size);
        freeSpaceForMessageRowUserMessage = screenWidth - userMessageWidth;
    }

    private int getDimen(int dimen) {
        return context.getResources().getDimensionPixelSize(dimen);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_OWN_MESSAGE: {
                View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_own_messsage,
                        parent, false);
                return new OwnMessageViewHolder(itemRow);
            }
            case VIEW_TYPE_SOMEONES_MESSAGE:{
                View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_someones_message,
                        parent, false);
                return new UserMessageViewHolder(itemRow);
            }
            case VIEW_TYPE_DATE_ROW:{
                View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_date_entry,
                        parent, false);
                return new DateViewHolder(itemRow);
            }
        }
        throw new IllegalArgumentException("No such view type in adapter");
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
        Entry entry = entries.get(position);
        switch (getItemViewType(position)) {
            case VIEW_TYPE_OWN_MESSAGE:
                bindOwnMessageHolder((OwnMessageViewHolder) holder, entry);
                break;
            case VIEW_TYPE_SOMEONES_MESSAGE:
                bindUserMessageHolder((UserMessageViewHolder) holder, entry);
                break;
            case VIEW_TYPE_DATE_ROW:
                bindDateEntryHolder((DateViewHolder) holder, entry.dateRowTitle);
                break;
        }
    }

    private void bindOwnMessageHolder(OwnMessageViewHolder holder, Entry entry) {
        StringBuilder messageText = new StringBuilder(entry.message.getMessage());
        if (DEBUG_PRINT_DATES_FOR_EACH_MESSAGE) {
            messageText.append("\n");
            messageText.append(manyDaysAgoDateEntryFormatter.format(entry.message.getDate()));
        }
        holder.messageTextView.setText(messageText.toString());

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (entry.previousEntryIsMessageFromSameUser) {
            params.setMargins(params.leftMargin, rowVerticalMargin, params.rightMargin, rowVerticalMargin);
            holder.messageTextView.setBackgroundResource(R.drawable.blue_bubble);
        } else {
            params.setMargins(params.leftMargin, 0, params.rightMargin, 0);
            holder.messageTextView.setBackgroundResource(R.drawable.blue_bubble_comics);
        }
    }

    private void bindUserMessageHolder(UserMessageViewHolder holder, Entry entry) {
        if (chatConversation.isGroupConversation()) {
            holder.nameTextView.setVisibility(View.VISIBLE);
            holder.nameTextView.setText(entry.message.getUser().getName());
        } else {
            holder.nameTextView.setVisibility(View.GONE);
        }
        StringBuilder messageText = new StringBuilder(entry.message.getMessage());
        if (DEBUG_PRINT_DATES_FOR_EACH_MESSAGE) {
            messageText.append("\n");
            messageText.append(manyDaysAgoDateEntryFormatter.format(entry.message.getDate()));
        }
        holder.messageTextView.setText(messageText.toString());

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (entry.previousEntryIsMessageFromSameUser) {
            params.setMargins(params.leftMargin, 0, params.rightMargin, 0);
            holder.avatarImageView.setVisibility(View.INVISIBLE);
            holder.messageTextView.setBackgroundResource(R.drawable.grey_bubble);
        } else {
            params.setMargins(params.leftMargin, rowVerticalMargin, params.rightMargin, rowVerticalMargin);
            holder.messageTextView.setBackgroundResource(R.drawable.grey_bubble_comics);
            holder.avatarImageView.setVisibility(View.VISIBLE);
            Picasso.with(context).load(entry.message.getUser()
                    .getAvatarUrl()).placeholder(android.R.drawable.ic_menu_compass)
                    .into(holder.avatarImageView);
        }
    }

    private void bindDateEntryHolder(DateViewHolder holder, String date) {
        holder.dateTextView.setText(date);
    }

    @Override public int getItemCount() {
        return entries.size();
    }

    @Override public int getItemViewType(int position) {
        return entries.get(position).viewType;
    }

    public void setChatConversation(ChatConversation chatConversation) {
        this.chatConversation = chatConversation;
        entries = new ArrayList<>();
        ChatDateEntryConstructor chatDateEntryConstructor = new ChatDateEntryConstructor();
        ChatMessage previousMessage = null;
        for (ChatMessage message : chatConversation.getMessages()) {
            Entry dateEntry = chatDateEntryConstructor.getDateEntryIfNeeded(message.getDate());
            if (dateEntry != null) {
                entries.add(dateEntry);
            }
            Entry messageEntry;
            if (message.getUser().equals(chatConversation.getConversationOwner())) {
                messageEntry = Entry.newOwnMessageEntry(message);
            } else {
                messageEntry = Entry.newUserMessageEntry(message);
            }
            if (previousMessage != null && dateEntry == null &&  previousMessage.getUser().equals(message.getUser())) {
                messageEntry.previousEntryIsMessageFromSameUser = true;
            }
            entries.add(messageEntry);
            previousMessage = message;
        }
    }

    private class ChatDateEntryConstructor {
        private Date previousMessageDate;

        public Entry getDateEntryIfNeeded(Date messageDate) {
            String dateString = null;
            int calendarDaysSincePreviousDate = 0;
            if (previousMessageDate != null) {
                calendarDaysSincePreviousDate = (int) ChatDateFormatter.calendarDaysBetweenDates(previousMessageDate, messageDate);
            }
            if ((previousMessageDate != null && calendarDaysSincePreviousDate > 0) || previousMessageDate == null) {
                int daysSinceToday = (int)ChatDateFormatter.calendarDaysBetweenDates(ChatDateFormatter.getToday()
                        .getTime(), messageDate);
                if (daysSinceToday == 0) {
                    dateString = todayDateEntryFormatter.format(messageDate);
                } else if (daysSinceToday == 1) {
                    dateString = yesterdayDateEntryFormatter.format(messageDate);
                } else if (daysSinceToday > 1 && daysSinceToday <= 4) {
                    dateString = fewDaysAgoDateEntryFormatter.format(messageDate);
                } else {
                    dateString = manyDaysAgoDateEntryFormatter.format(messageDate);
                }
            }
            previousMessageDate = messageDate;
            if (dateString == null) {
                return null;
            } else {
                return Entry.newDateEntry(dateString);
            }
        }
    }
}

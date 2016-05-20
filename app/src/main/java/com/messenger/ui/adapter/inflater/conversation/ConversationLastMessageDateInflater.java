package com.messenger.ui.adapter.inflater.conversation;

import android.view.View;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.ui.adapter.inflater.ViewInflater;
import com.messenger.ui.util.chat.ChatDateUtils;
import com.worldventures.dreamtrips.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.InjectView;

public class ConversationLastMessageDateInflater extends ViewInflater {

    @InjectView(R.id.conversation_last_message_date_textview)
    TextView lastMessageDateTextView;

    private SimpleDateFormat todayDateFormat;
    private SimpleDateFormat moreThanTwoDaysAgoFormat;

    @Override
    public void setView(View rootView) {
        super.setView(rootView);
        todayDateFormat = new SimpleDateFormat(context
                .getString(R.string.conversation_list_last_message_date_format_today));
        moreThanTwoDaysAgoFormat = new SimpleDateFormat(context
                .getString(R.string.conversation_list_last_message_date_format_more_than_one_day_ago));
    }

    public void setDate(DataConversation conversation) {
        lastMessageDateTextView.setText(formatLastConversationMessage(new Date(conversation.getLastActiveDate())));
    }

    public String formatLastConversationMessage(Date date) {
        Calendar today = ChatDateUtils.getToday();

        if (date.after(today.getTime())) {
            return todayDateFormat.format(date);
        } else {
            Calendar yesterday = today;
            yesterday.roll(Calendar.DAY_OF_YEAR, false);
            if (date.after(yesterday.getTime())) {
                return context.getString(R.string.conversation_list_last_message_date_format_yesterday);
            } else {
                return moreThanTwoDaysAgoFormat.format(date);
            }
        }
    }
}

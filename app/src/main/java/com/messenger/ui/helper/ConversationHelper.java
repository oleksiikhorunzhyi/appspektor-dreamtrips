package com.messenger.ui.helper;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.ConversationType;
import com.worldventures.dreamtrips.R;

import java.util.List;

public class ConversationHelper {

    public void setTitle(final TextView target, DataConversation conversation, List<DataUser> members, boolean withGroupSize) {
        if (members == null || members.isEmpty()) {
            return;
        }
        String initialTitle;
        switch (conversation.getType()) {
            case ConversationType.CHAT:
                initialTitle = members.get(0).getName();
                break;
            case ConversationType.GROUP:
            default:
                initialTitle = conversation.getSubject();
                if (TextUtils.isEmpty(initialTitle)) {
                    initialTitle = obtainDefaultGroupChatSubject(members);
                }
                break;
        }
        final int usersCount = members.size();
        if (usersCount == 1 || !withGroupSize) {
            target.setText(initialTitle);
        } else if (withGroupSize) {
            final String fInitialTitle = initialTitle;
            Runnable textSetter = () -> {
                CharSequence truncatedSubject = TextUtils.ellipsize(fInitialTitle, target.getPaint(), target.getMeasuredWidth() / 4f * 3f, TextUtils.TruncateAt.END);
                StringBuilder titleBuilder = new StringBuilder(truncatedSubject);
                titleBuilder.append(' ').append("(").append(usersCount).append(")");
                target.setText(titleBuilder.toString());
            };
            runTaskAfterMeasure(target, textSetter);
        }
    }

    public void setSubtitle(TextView target, DataConversation conversation, List<DataUser> members) {
        if (members == null || members.isEmpty()) {
            return;
        }
        CharSequence subtitle;
        switch (conversation.getType()) {
            case ConversationType.CHAT:
                int substringRes = members.get(0).isOnline() ? R.string.chat_subtitle_format_single_chat_online : R.string.chat_subtitle_format_single_chat_offline;
                subtitle = target.getResources().getText(substringRes);
                break;
            case ConversationType.GROUP:
            default:
                int online = Queryable.from(members).count(DataUser::isOnline);
                subtitle = target.getResources().getString(R.string.chat_subtitle_format_group_chat_format, members.size(), online);
                break;
        }
        target.setText(subtitle);
    }

    public void setGroupChatTitle(TextView target, String subject, int userCount) {
        if (TextUtils.isEmpty(subject)) return;

        Runnable runnable = () -> {
            String counterLabel = String.format(" (%s)", userCount);
            double widthOfEllipsize = getTextWidthWithMargins(counterLabel, target.getTextSize()) * 1.2; // 20 % infelicity
            CharSequence ellipsizedText = TextUtils.ellipsize(subject, target.getPaint(), target.getWidth() - (int) widthOfEllipsize, TextUtils.TruncateAt.END);
            target.setText(ellipsizedText + counterLabel);
        };

        runTaskAfterMeasure(target, runnable);
    }

    private void runTaskAfterMeasure(TextView target, Runnable runTask){
        if (target.getMeasuredWidth() == 0) {
            target.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    target.getViewTreeObserver().removeOnPreDrawListener(this);
                    runTask.run();
                    return true;
                }
            });
        } else {
            runTask.run();
        }
    }

    private int getTextWidthWithMargins(String text, float textSize) {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);

        return bounds.right;
    }

    public static boolean isGroup(DataConversation conversation) {
        return conversation.getType() != null && !conversation.getType().equals(ConversationType.CHAT);
    }

    public static boolean isSingleChat(DataConversation conversation) {
        return ConversationType.CHAT.equals(conversation.getType());
    }

    public static boolean isTripChat(DataConversation conversation) {
        return TextUtils.equals(conversation.getType() , ConversationType.TRIP);
    }

    public static boolean isOwner(DataConversation conversation, DataUser user) {
        return conversation.getOwnerId() != null && conversation.getOwnerId().equals(user.getId());
    }

    public static String obtainDefaultGroupChatSubject(List<DataUser> members) {
        return Queryable.from(members).map(DataUser::getFirstName).joinStrings(", ");
    }
}

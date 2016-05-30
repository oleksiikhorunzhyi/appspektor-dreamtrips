package com.messenger.ui.helper;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.ConversationType;
import com.worldventures.dreamtrips.R;

import java.util.List;

public final class ConversationUIHelper {

    private ConversationUIHelper() {
    }

    public static void setTitle(final TextView textView, DataConversation conversation, List<DataUser> members, boolean withGroupSize) {
        if (isEmptyMembers(members)) return;

        String initialTitle = ConversationHelper.obtainConversationSubject(conversation, members);

        if (ConversationHelper.isSingleChat(conversation) || !withGroupSize) {
            textView.setText(initialTitle);
        } else {
            Runnable textSetter = () -> textView.setText(obtainFittingTitle(textView, initialTitle, members.size()));
            runTaskAfterMeasure(textView, textSetter);
        }
    }

    private static String obtainFittingTitle(TextView textView, String initialTitle, int memberCount) {
        CharSequence truncatedSubject = TextUtils.ellipsize(initialTitle, textView.getPaint(), textView.getMeasuredWidth() / 4f * 3f, TextUtils.TruncateAt.END);
        return truncatedSubject.toString() + ' ' + "(" + memberCount + ")";
    }

    public static void setSubtitle(TextView target, DataConversation conversation, List<DataUser> members) {
        if (isEmptyMembers(members)) return;

        CharSequence subtitle;
        Resources res =  target.getResources();
        switch (conversation.getType()) {
            case ConversationType.CHAT:
                int substringRes = members.get(0).isOnline() ?
                        R.string.chat_subtitle_format_single_chat_online
                        : R.string.chat_subtitle_format_single_chat_offline;
                subtitle = res.getText(substringRes);
                break;
            case ConversationType.GROUP:
            default:
                if (ConversationHelper.isAbandoned(conversation)) {
                    subtitle = res.getString(R.string.chat_subtitle_format_group_chat_format_closed,
                            members.size());
                } else {
                    int onlineMembersCount = Queryable.from(members).count(DataUser::isOnline);
                    subtitle = res.getString(R.string.chat_subtitle_format_group_chat_format,
                            members.size(), onlineMembersCount);
                }
                break;
        }
        target.setText(subtitle);
    }

    public static void setGroupChatTitle(TextView textView, String subject, int userCount) {
        if (TextUtils.isEmpty(subject)) return;

        Runnable runnable = () -> {
            String counterLabel = String.format(" (%s)", userCount);
            double widthOfEllipsize = getTextWidthWithMargins(counterLabel, textView.getTextSize()) * 1.2; // 20 % infelicity
            CharSequence ellipsizedText = TextUtils.ellipsize(subject, textView.getPaint(), textView.getWidth() - (int) widthOfEllipsize, TextUtils.TruncateAt.END);
            textView.setText(ellipsizedText + counterLabel);
        };

        runTaskAfterMeasure(textView, runnable);
    }

    private static void runTaskAfterMeasure(TextView textView, Runnable runTask) {
        if (textView.getMeasuredWidth() == 0) {
            textView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    textView.getViewTreeObserver().removeOnPreDrawListener(this);
                    runTask.run();
                    return true;
                }
            });
        } else {
            runTask.run();
        }
    }

    private static int getTextWidthWithMargins(String text, float textSize) {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);

        return bounds.right;
    }

    private static boolean isEmptyMembers (@Nullable List<DataUser> members) {
        return members == null || members.isEmpty();
    }
}

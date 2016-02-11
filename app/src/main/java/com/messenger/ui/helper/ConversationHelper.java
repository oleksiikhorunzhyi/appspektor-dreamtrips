package com.messenger.ui.helper;

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
                if (isEmptyString(initialTitle)) {
                    initialTitle = Queryable.from(members).map(u -> u.getUserName().split("\\s")[0]).joinStrings(", ");
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
            if (target.getMeasuredWidth() == 0) {
                target.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        target.getViewTreeObserver().removeOnPreDrawListener(this);
                        textSetter.run();
                        return true;
                    }
                });
            } else {
                textSetter.run();
            }
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

    public static boolean isGroup(DataConversation conversation) {
        return conversation.getType() != null && !conversation.getType().equals(ConversationType.CHAT);
    }

    public static boolean isOwner(DataConversation conversation, DataUser user) {
        return conversation.getOwnerId() != null && conversation.getOwnerId().equals(user.getId());
    }

    private boolean isEmptyString(CharSequence charSequence) {
        // use "null" check because server sends us null as "null" string sometimes
        return TextUtils.isEmpty(charSequence) || charSequence.equals("null");
    }
}
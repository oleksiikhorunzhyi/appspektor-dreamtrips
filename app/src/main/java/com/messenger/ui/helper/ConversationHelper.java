package com.messenger.ui.helper;

import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.worldventures.dreamtrips.R;

import java.util.List;

public class ConversationHelper {

    public void setTitle(final TextView target, Conversation conversation, List<User> members) {
        if (members == null || members.isEmpty()) {
            return;
        }
        String initialTitle = null;
        switch (conversation.getType()) {
            case Conversation.Type.CHAT:
                initialTitle = members.get(0).getName();
                break;
            case Conversation.Type.GROUP:
                initialTitle = conversation.getSubject();
                if (TextUtils.isEmpty(initialTitle)) {
                    initialTitle = Queryable.from(members).map(User::getUserName).joinStrings(", ");
                }
                break;
        }
        final int usersCount = members.size();
        if (usersCount == 1) {
            target.setText(initialTitle);
        } else {
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

    public void setSubtitle(TextView target, Conversation conversation, List<User> members) {
        if (members == null || members.isEmpty()) {
            return;
        }
        CharSequence subtitle = null;
        switch (conversation.getType()) {
            case Conversation.Type.CHAT:
                int substringRes = members.get(0).isOnline() ? R.string.chat_subtitle_format_single_chat_online : R.string.chat_subtitle_format_single_chat_offline;
                subtitle = target.getResources().getText(substringRes);
                break;
            case Conversation.Type.GROUP:
                int online = Queryable.from(members).count(User::isOnline);
                subtitle = target.getResources().getString(R.string.chat_subtitle_format_group_chat_format, online);
                break;
        }
        target.setText(subtitle);
    }
}

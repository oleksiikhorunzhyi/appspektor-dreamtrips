package com.messenger.ui.util;

import android.content.Context;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.util.Pair;
import android.view.Menu;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import rx.Observable;
import rx.functions.Func2;

public class ChatContextualMenuProvider {

    private Context context;
    private UsersDAO usersDAO;

    public ChatContextualMenuProvider(Context context, UsersDAO usersDAO) {
        this.context = context;
        this.usersDAO = usersDAO;
    }

    public Observable<Menu> provideMenu(DataMessage message, DataUser currentUser,
                                        Observable<DataConversation> conversationObservable,
                                        Observable<DataAttachment> attachmentObservable) {
        return Observable
                .combineLatest(
                        conversationObservable.first(), attachmentObservable.first(),
                        usersDAO.getUserById(message.getFromId()).first(), QueryResult::new
                )
                .compose(new IoToMainComposer<>())
                .map(queryResult -> {
                    Menu menu = new MenuBuilder(context);
                    new SupportMenuInflater(context).inflate(R.menu.menu_chat_contextual, menu);

                    // cannot chat with yourself, doesn't make sense to start new 1 : 1 chat with the same user either
                    if (currentUser.getId().equals(message.getFromId())
                            || ConversationHelper.isSingleChat(queryResult.conversation)
                            || queryResult.messageAuthor == null) {
                        menu.removeItem(R.id.action_start_chat);
                    } else {
                        String title = String.format(context
                                .getString(R.string.chat_contextual_start_chat_format), queryResult.messageAuthor.getFirstName());
                        menu.findItem(R.id.action_start_chat).setTitle(title);
                    }
                    // attachment is not null, copying of attachment is not supported
                    if (queryResult.attachment != null) {
                        menu.removeItem(R.id.action_copy_message);
                    }
                    return menu;
                });
    }

    private static class QueryResult {
        private DataConversation conversation;
        private DataAttachment attachment;
        private DataUser messageAuthor;

        public QueryResult(DataConversation conversation, DataAttachment attachment, DataUser messageAuthor) {
            this.conversation = conversation;
            this.attachment = attachment;
            this.messageAuthor = messageAuthor;
        }
    }
}

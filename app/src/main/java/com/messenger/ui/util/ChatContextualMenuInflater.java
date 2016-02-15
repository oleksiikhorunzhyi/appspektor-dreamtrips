package com.messenger.ui.util;

import android.content.Context;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.PopupMenu;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import rx.Observable;

public class ChatContextualMenuInflater {

    private Context context;

    public ChatContextualMenuInflater(Context context) {
        this.context = context;
    }

    public Observable<Menu> inflateMenu(DataMessage message, DataUser user,
                                        Observable<DataConversation> conversationObservable,
                                        Observable<DataAttachment> attachmentObservable) {
        return Observable.combineLatest(conversationObservable.first().compose(new IoToMainComposer<>()),
                attachmentObservable.first().compose(new IoToMainComposer<>()),
                (conversation, attachment)
                        -> new Pair<DataConversation, DataAttachment>(conversation, attachment) {})
                .map(pair -> {
                    Menu menu = new MenuBuilder(context);
                    new SupportMenuInflater(context).inflate(R.menu.menu_chat_contextual, menu);

                    // cannot chat with yourself, doesn't make sense to start new 1 : 1 chat with the same user either
                    if (user.getId().equals(message.getFromId()) || ConversationHelper.isSingleChat(pair.first)) {
                        menu.removeItem(R.id.action_start_chat);
                    }
                    // attachment is not null, copying of attachment is not supported
                    if (pair.second != null) {
                        menu.removeItem(R.id.action_copy_message);
                    }
                    return menu;
                });
    }
}

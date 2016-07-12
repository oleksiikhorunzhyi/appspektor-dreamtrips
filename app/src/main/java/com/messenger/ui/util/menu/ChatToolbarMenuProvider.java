package com.messenger.ui.util.menu;

import android.text.TextUtils;
import android.view.Menu;

import com.messenger.entities.DataUser;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import javax.inject.Inject;

import rx.Observable;

public class ChatToolbarMenuProvider {

    private final ConversationsDAO conversationDAO;
    private final DataUser currentUser;

    @Inject
    public ChatToolbarMenuProvider(ConversationsDAO conversationDAO, DataUser currentUser) {
        this.conversationDAO = conversationDAO;
        this.currentUser = currentUser;
    }

    public Observable<Menu> provideChatMenu(String conversationId, Menu menu) {
       return conversationDAO
                .getConversation(conversationId).take(1)
                .compose(new IoToMainComposer<>())
                .map(conversation -> {
                    boolean addVisible = ConversationHelper.isSingleChat(conversation) || (ConversationHelper.isGroup(conversation)
                            && TextUtils.equals(currentUser.getId(), conversation.getOwnerId()));
                    menu.findItem(R.id.action_add).setVisible(addVisible);
                    menu.findItem(R.id.action_settings).setVisible(true);
                    return menu;
                });
    }
}

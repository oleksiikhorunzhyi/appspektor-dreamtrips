package com.messenger.ui.presenter;

import android.support.annotation.StringDef;

import com.messenger.entities.Conversation;
import com.messenger.entities.User;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.view.conversation.ConversationListScreen;
import com.messenger.ui.viewstate.ConversationListViewState;

public interface ConversationListScreenPresenter extends MessengerPresenter<ConversationListScreen,
        ConversationListViewState> {
    User getUser();

    ParticipantsDAO getParticipantsDao();

    void onConversationSelected(Conversation conversation);

    void onDeleteButtonPressed(Conversation conversation);

    void onDeletionConfirmed(Conversation conversation);

    void onMoreOptionsButtonPressed(Conversation conversation);

    void onMarkAsUnreadButtonPressed(Conversation conversation);

    void onTurnOffNotificationsButtonPressed(Conversation conversation);

    void onConversationsDropdownSelected(ChatTypeItem selectedItem);

    void onConversationsSearchFilterSelected(String searchFilter);

    class ChatTypeItem {
        public static final String ALL_CHATS = "all";
        public static final String GROUP_CHATS = "group";

        @StringDef({ALL_CHATS, GROUP_CHATS})
        public @interface ChatsType {
        }

        private String title;
        private String type;


        public ChatTypeItem(@ChatsType String type, String title) {
            this.title = title;
            this.type = type;
        }

        @ChatsType
        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}


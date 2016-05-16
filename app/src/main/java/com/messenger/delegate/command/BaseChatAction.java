package com.messenger.delegate.command;

import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import rx.Observable;

public abstract class BaseChatAction<Result> extends CommandActionBase<Result> implements InjectableAction {
    protected final DataConversation conversation;

    @Inject MessengerServerFacade messengerServerFacade;

    protected BaseChatAction(DataConversation conversation) {
        this.conversation = conversation;
    }

    public DataConversation getConversation() {
        return conversation;
    }

    protected Observable<MultiUserChat> createMultiChat() {
      return messengerServerFacade.getChatManager()
              .createMultiUserChatObservable(conversation.getId(), messengerServerFacade.getUsername());
    }

    protected Chat getChat() {
        if (ConversationHelper.isSingleChat(conversation)) {
            return messengerServerFacade.getChatManager().createSingleUserChat(null, conversation.getId());
        } else {
            return messengerServerFacade.getChatManager().createMultiUserChat(conversation.getId(), conversation.getOwnerId());
        }
    }
}

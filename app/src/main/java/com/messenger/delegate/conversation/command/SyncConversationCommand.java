package com.messenger.delegate.conversation.command;


import com.messenger.delegate.conversation.helper.ConversationSyncHelper;
import com.messenger.delegate.user.UsersDelegate;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SyncConversationCommand extends CommandActionBase<Conversation> implements InjectableAction {

    @Inject Janet janet;
    @Inject UsersDelegate usersDelegate;
    @Inject ConversationSyncHelper conversationSyncHelper;

    private final String conversationId;

    public SyncConversationCommand(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    protected void run(CommandCallback<Conversation> callback) throws Throwable {
        janet.createPipe(LoadConversationCommand.class)
                .createObservableSuccess(new LoadConversationCommand(conversationId))
                .map(CommandActionBase::getResult)
                .flatMap(conversation ->
                        usersDelegate.loadAndSaveUsers(ConversationHelper.getUsersFromConversation(conversation))
                                .map(fetchUsersDataCommand -> conversation)
                )
                .subscribe(conversation -> {
                    conversationSyncHelper.process(conversation);
                    callback.onSuccess(conversation);
                }, callback::onFail);
    }

}

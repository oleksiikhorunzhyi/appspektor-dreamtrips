package com.messenger.delegate.conversation.command;


import com.messenger.delegate.conversation.helper.ConversationSyncHelper;
import com.messenger.delegate.roster.FetchUsersDataCommand;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class SyncConversationsCommand extends CommandActionBase<List<Conversation>> implements InjectableAction {

    @Inject Janet janet;
    @Inject ConversationSyncHelper conversationSyncHelper;

    @Override
    protected void run(CommandCallback<List<Conversation>> callback) throws Throwable {
        janet.createPipe(LoadConversationsCommand.class)
                .createObservableSuccess(new LoadConversationsCommand())
                .map(CommandActionBase::getResult)
                .flatMap(conversations -> janet.createPipe(FetchUsersDataCommand.class)
                        .createObservableSuccess(FetchUsersDataCommand.from(ConversationHelper.getUsersFromConversations(conversations)))
                        .flatMap(action -> Observable.just(conversations))
                )
                .subscribe(conversations -> {
                    conversationSyncHelper.process(conversations);
                    callback.onSuccess(conversations);
                }, callback::onFail);
    }

}

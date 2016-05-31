package com.messenger.delegate.conversation.command;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.model.Conversation;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import io.techery.janet.command.annotations.CommandAction;


@CommandAction
public class LoadConversationsCommand extends CommandActionBase<List<Conversation>> implements InjectableAction {

    @Inject MessengerServerFacade messengerServerFacade;

    @Override
    protected void run(CommandCallback<List<Conversation>> callback) throws Throwable {
        messengerServerFacade.getLoaderManager()
                .createConversationsLoader()
                .load()
                .subscribe(callback::onSuccess, callback::onFail);
    }
}

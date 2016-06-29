package com.messenger.delegate.chat.command;

import com.messenger.messengerservers.ChatExtensions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import timber.log.Timber;

@CommandAction
public class ClearChatCommand extends Command<Void> implements InjectableAction {

    @Inject ChatExtensions chatExtensions;

    private final String conversationId;

    public ClearChatCommand(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        chatExtensions
                .clearChat(conversationId, System.currentTimeMillis())
                .doOnNext(event -> Timber.d("CLEAR EVENT = %s", event))
                // TODO handle result
                .map(clearChatEvent -> (Void) null)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}

package com.messenger.delegate.command;

import com.messenger.entities.DataConversation;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RemoveAvatarCommand extends ChangeAvatarCommand {

    public RemoveAvatarCommand(String conversationId) {
        super(conversationId);
    }

    @Override
    protected void run(CommandCallback<DataConversation> callback) {
        sendAvatar(null)
                .subscribe(conversation -> uploadComplete(conversation, callback), callback::onFail);
    }
}

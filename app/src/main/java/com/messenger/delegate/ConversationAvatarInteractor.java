package com.messenger.delegate;

import com.messenger.delegate.command.avatar.RemoveChatAvatarCommand;
import com.messenger.delegate.command.avatar.SendChatAvatarCommand;
import com.messenger.delegate.command.avatar.SetChatAvatarCommand;
import com.messenger.delegate.command.avatar.UploadChatAvatarCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

@Singleton
public class ConversationAvatarInteractor {
    private final ActionPipe<SendChatAvatarCommand> sendChatAvatarCommandActionPipe;
    private final ActionPipe<SetChatAvatarCommand> setChatAvatarCommandPipe;
    private final ActionPipe<RemoveChatAvatarCommand> removeChatAvatarCommandPipe;

    @Inject
    ConversationAvatarInteractor(Janet janet) {
        this.sendChatAvatarCommandActionPipe = janet.createPipe(SendChatAvatarCommand.class);
        this.setChatAvatarCommandPipe = janet.createPipe(SetChatAvatarCommand.class);
        this.removeChatAvatarCommandPipe = janet.createPipe(RemoveChatAvatarCommand.class);
    }

    public ActionPipe<SendChatAvatarCommand> getSendChatAvatarCommandPipe() {
        return sendChatAvatarCommandActionPipe;
    }

    public ActionPipe<SetChatAvatarCommand> getSetChatAvatarCommandPipe() {
        return setChatAvatarCommandPipe;
    }

    public ActionPipe<RemoveChatAvatarCommand> getRemoveChatAvatarCommandPipe() {
        return removeChatAvatarCommandPipe;
    }
}

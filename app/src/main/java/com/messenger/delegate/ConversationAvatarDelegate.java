package com.messenger.delegate;

import com.messenger.delegate.command.ChangeAvatarCommand;
import com.messenger.delegate.command.RemoveAvatarCommand;
import com.messenger.delegate.command.SetAvatarUploadCommand;
import com.messenger.entities.DataConversation;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.ReadOnlyActionPipe;

@Singleton
public class ConversationAvatarDelegate {
    private final ActionPipe<ChangeAvatarCommand> changeAvatarCommandActionPipe;
    private final ReadOnlyActionPipe<ChangeAvatarCommand> readChangeAvatarCommandActionPipe;

    @Inject ConversationAvatarDelegate(Janet janet) {
        this.changeAvatarCommandActionPipe = janet.createPipe(ChangeAvatarCommand.class);
        this.readChangeAvatarCommandActionPipe = changeAvatarCommandActionPipe.asReadOnly();
    }

    public void setAvatarToConversation(String conversationId, String photoPath) {
        changeAvatarCommandActionPipe.send(new SetAvatarUploadCommand(conversationId, photoPath));}

    public void removeAvatar(String conversationId) {
        changeAvatarCommandActionPipe.send(new RemoveAvatarCommand(conversationId));
    }

    public ReadOnlyActionPipe<ChangeAvatarCommand> getReadChangeAvatarCommandActionPipe() {
        return readChangeAvatarCommandActionPipe;
    }
}

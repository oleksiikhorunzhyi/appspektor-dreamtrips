package com.messenger.delegate.command.avatar;

import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.entities.DataConversation;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SetChatAvatarCommand extends BaseChatCommand<DataConversation> implements InjectableAction {

    @Inject Janet janet;
    private String imagePath;

    public SetChatAvatarCommand(String conversationId, String imagePath) {
        super(conversationId);
        this.imagePath = imagePath;
    }

    @Override
    protected void run(CommandCallback<DataConversation> callback) throws Throwable {
        janet.createPipe(UploadChatAvatarCommand.class)
                .createObservableResult(new UploadChatAvatarCommand(imagePath))
                .map(Command::getResult)
                .flatMap(avatar ->
                        janet.createPipe(SendChatAvatarCommand.class)
                            .createObservableResult(new SendChatAvatarCommand(conversationId, avatar))
                            .map(Command::getResult)
                )
                .subscribe(callback::onSuccess, callback::onFail);
    }
}

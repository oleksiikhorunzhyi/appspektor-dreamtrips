package com.messenger.delegate.command;

import com.messenger.entities.DataConversation;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyManager;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;

@CommandAction
public class SetAvatarUploadCommand extends ChangeAvatarCommand {
    @Inject UploaderyManager uploaderyManager;

    private final String imagePath;

    public SetAvatarUploadCommand(DataConversation conversation, String imagePath) {
        super(conversation);
        this.imagePath = imagePath;
    }

    @Override
    protected void run(CommandCallback<DataConversation> callback) {
        uploaderyManager.getUploadImagePipe()
                .createObservable(new SimpleUploaderyCommand(imagePath))
                .doOnNext(state -> handleUploadingState(state, callback))
                .compose(new ActionStateToActionTransformer<>())
                .flatMap(this::sendCommandResult)
                .subscribe(conversation -> uploadComplete(conversation, callback), callback::onFail);
    }

    private void handleUploadingState(ActionState<UploaderyImageCommand> actionState, CommandCallback<DataConversation> callback) {
        switch (actionState.status) {
            case FAIL:
                callback.onFail(actionState.exception);
                break;
            case PROGRESS:
                callback.onProgress(actionState.progress);
                break;
        }
    }

    private Observable<DataConversation> sendCommandResult(UploaderyImageCommand uploaderyCommand) {
        SimpleUploaderyCommand command = (SimpleUploaderyCommand) uploaderyCommand;
        return sendAvatar(command.getResult().getPhotoUploadResponse().getLocation());
    }
}

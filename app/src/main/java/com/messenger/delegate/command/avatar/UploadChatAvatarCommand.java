package com.messenger.delegate.command.avatar;

import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyManager;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;

@CommandAction
public class UploadChatAvatarCommand extends Command<String> implements InjectableAction {

    private final String imagePath;

    public UploadChatAvatarCommand(String imagePath) {
        this.imagePath = imagePath;
    }

    @Inject
    UploaderyManager uploaderyManager;

    @Override
    protected void run(CommandCallback<String> callback) {
        uploaderyManager.getUploadImagePipe()
                .createObservable(new SimpleUploaderyCommand(imagePath))
                .doOnNext(state -> handleUploadingState(state, callback))
                .compose(new ActionStateToActionTransformer<>())
                .map(command -> ((SimpleUploaderyCommand)command))
                .map(simpleCommand -> simpleCommand.getResult().getPhotoUploadResponse().getLocation())
                .subscribe(callback::onSuccess, callback::onFail);
    }

    private void handleUploadingState(ActionState<UploaderyImageCommand> actionState, CommandCallback<String> callback) {
        switch (actionState.status) {
            case FAIL:
                callback.onFail(actionState.exception);
                break;
            case PROGRESS:
                callback.onProgress(actionState.progress);
                break;
        }
    }
}

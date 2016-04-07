package com.worldventures.dreamtrips.core.api.uploadery;

import io.techery.janet.ActionState;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;

@CommandAction
public class SimpleUploaderyCommand extends UploaderyImageCommand<UploadImageAction> {

    public SimpleUploaderyCommand(String filePath) {
        super(filePath, 0);
    }

    public SimpleUploaderyCommand(String filePath, int commandId) {
        super(filePath, commandId);
    }

    @Override
    protected Observable.Transformer<ActionState<UploadImageAction>, UploadImageAction> nextAction() {
        return uploadImageActionObservable -> uploadImageActionObservable
                .compose(new ActionStateToActionTransformer<>());
    }
}

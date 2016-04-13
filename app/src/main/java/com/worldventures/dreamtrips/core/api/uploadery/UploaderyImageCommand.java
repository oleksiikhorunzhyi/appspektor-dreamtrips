package com.worldventures.dreamtrips.core.api.uploadery;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public abstract class UploaderyImageCommand<T> extends BaseUploadImageCommand<T> {
    private final int commandId;

    @ForApplication
    @Inject
    Context context;

    @Inject
    Janet janet;

    private final String filePath;

    public UploaderyImageCommand(String filePath, int commandId) {
        this.commandId = commandId;
        this.filePath = filePath;
    }

    @Override
    protected void run(CommandCallback<T> callback) {
        getFileObservable(context, filePath)
                .flatMap(this::upload)
                .compose(nextAction())
                .subscribe(callback::onSuccess, callback::onFail);

    }

    public int getCommandId() {
        return commandId;
    }

    public String getFilePath() {
        return filePath;
    }

    protected Observable<ActionState<UploadImageAction>> upload(File file) {
        return janet
                .createPipe(UploadImageAction.class, Schedulers.io())
                .createObservable(new UploadImageAction(file));
    }

    protected abstract Observable.Transformer<ActionState<UploadImageAction>, T> nextAction();

}

package com.worldventures.dreamtrips.core.api.uploadery;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public abstract class UploaderyImageCommand<T> extends BaseUploadImageCommand<T> implements InjectableAction {
    private final int commandId;

    @ForApplication @Inject Context context;
    @Inject Janet janet;
    @Inject StaticPageProvider staticPageProvider;

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
        String uploaderyUrl = staticPageProvider.getUploaderyUrl();
        try {
            return janet
                    .createPipe(UploadImageAction.class, Schedulers.io())
                    .createObservable(new UploadImageAction(uploaderyUrl, file));
        } catch (IOException e) {
            return Observable.error(e);
        }
    }

    protected abstract Observable.Transformer<ActionState<UploadImageAction>, T> nextAction();
}

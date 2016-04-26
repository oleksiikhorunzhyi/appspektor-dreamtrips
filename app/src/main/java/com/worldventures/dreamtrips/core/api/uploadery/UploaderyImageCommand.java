package com.worldventures.dreamtrips.core.api.uploadery;

import android.content.Context;

import com.messenger.util.SessionHolderHelper;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@CommandAction
public abstract class UploaderyImageCommand<T> extends BaseUploadImageCommand<T> {
    private final int commandId;

    @ForApplication
    @Inject
    Context context;

    @Inject
    SessionHolder<UserSession> userSessionHolder;

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
        if (!SessionHolderHelper.hasEntity(userSessionHolder)) {
            throw new IllegalStateException("User session is not present");
        }

        String uploaderyUrl = userSessionHolder.get().get().getGlobalConfig().getUrls().getProduction().getUploaderyBaseURL();
        return janet
                .createPipe(UploadImageAction.class, Schedulers.io())
                .createObservable(new UploadImageAction(uploaderyUrl, file));
    }

    protected abstract Observable.Transformer<ActionState<UploadImageAction>, T> nextAction();

}

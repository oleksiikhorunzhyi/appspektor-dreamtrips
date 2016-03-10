package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import rx.Subscriber;
import rx.functions.Action1;

public class PhotoUploadSubscriber extends Subscriber<UploadTask> {

    private Action1<UploadTask> onSuccess;
    private Action1<UploadTask> onError;
    private Action1<UploadTask> onProgress;
    private Action1<UploadTask> onCancel;
    private Action1<UploadTask> beforeEach;
    private Action1<UploadTask> afterEach;

    public PhotoUploadSubscriber() {
    }

    public PhotoUploadSubscriber onSuccess(Action1<UploadTask> onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }

    public PhotoUploadSubscriber onError(Action1<UploadTask> onError) {
        this.onError = onError;
        return this;
    }

    public PhotoUploadSubscriber onProgress(Action1<UploadTask> onProgress) {
        this.onProgress = onProgress;
        return this;
    }

    public PhotoUploadSubscriber onCancel(Action1<UploadTask> onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public PhotoUploadSubscriber beforeEach(Action1<UploadTask> onEach) {
        this.beforeEach = onEach;
        return this;
    }

    public PhotoUploadSubscriber afterEach(Action1<UploadTask> afterEach) {
        this.afterEach = afterEach;
        return this;
    }

    @Override
    public void onNext(UploadTask uploadTask) {
        if (beforeEach != null) beforeEach.call(uploadTask);
        //
        switch (uploadTask.getStatus()) {
            case STARTED:
                if (onProgress != null) onProgress.call(uploadTask);
                break;
            case COMPLETED:
                if (onSuccess != null) onSuccess.call(uploadTask);
                break;
            case FAILED:
                if (onError != null) onError.call(uploadTask);
                break;
            case CANCELED:
                if (onCancel != null) onCancel.call(uploadTask);
                break;
        }
        if (afterEach != null) afterEach.call(uploadTask);
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) { // TODO ???
    }
}
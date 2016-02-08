package com.messenger.delegate;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import rx.Observable;
import rx.Subscriber;

public class RxTransferObserver implements Observable.OnSubscribe<Void> {
    private final TransferObserver uploadObservable;

    private RxTransferObserver(TransferObserver uploadObservable) {
        this.uploadObservable = uploadObservable;
    }

    public static Observable<Void> bind(TransferObserver uploadObservable) {
        return Observable.create(new RxTransferObserver(uploadObservable));
    }

    @Override
    public void call(Subscriber<? super Void> subscriber) {
        uploadObservable.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    subscriber.onNext((Void) null);
                    subscriber.onCompleted();
                } else if (state == TransferState.FAILED){
                    subscriber.onError(new UploadFailedException());
                } else if (state == TransferState.WAITING_FOR_NETWORK) {
                    subscriber.onError(new UploadProblemException());
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public static class UploadFailedException extends Exception {}

    public static class UploadProblemException extends Exception {}
}
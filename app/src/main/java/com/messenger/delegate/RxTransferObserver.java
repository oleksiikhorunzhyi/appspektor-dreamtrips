package com.messenger.delegate;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import rx.Observable;
import rx.Subscriber;

public class RxTransferObserver implements Observable.OnSubscribe<TransferObserver> {
    private final TransferObserver uploadObserver;

    private RxTransferObserver(TransferObserver uploadObservable) {
        this.uploadObserver = uploadObservable;
    }

    public static Observable<TransferObserver> bind(TransferObserver uploadObservable) {
        return Observable
                .create(new RxTransferObserver(uploadObservable))
                .doOnUnsubscribe(uploadObservable::cleanTransferListener);
    }

    @Override
    public void call(Subscriber<? super TransferObserver> subscriber) {
        if (uploadObserver.getState().equals(TransferState.COMPLETED)) {
            subscriber.onNext(uploadObserver);
            subscriber.onCompleted();
            return;
        }

        if (uploadObserver.getState().equals(TransferState.FAILED)) {
            subscriber.onError(new Exception("Something went wrong during uploading"));
            return;
        }

        if (uploadObserver.getState().equals(TransferState.CANCELED)) {
            subscriber.onError(new Exception("Image uploading was canceled"));
            return;
        }

        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                subscriber.onNext(uploadObserver);
                //
                if (state == TransferState.COMPLETED) {
                    subscriber.onCompleted();
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
}
package com.messenger.delegate;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import rx.Observable;
import rx.Subscriber;

public class RxTransferObserver {

    public static Observable<TransferObserver> bind(TransferObserver uploadObservable) {
        return Observable.<TransferObserver>create(subscriber ->
                uploadObservable.setTransferListener(createTransferListener(subscriber, uploadObservable)))
                .doOnUnsubscribe(() -> uploadObservable.cleanTransferListener());
    }

    private static TransferListener createTransferListener (Subscriber<? super TransferObserver> subscriber, TransferObserver uploadObservable){
        return new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                subscriber.onNext(uploadObservable);
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                subscriber.onNext(uploadObservable);
            }
        };
    }

}
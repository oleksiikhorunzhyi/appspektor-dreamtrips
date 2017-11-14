package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class DtlToolbarTransactinoClicksOnSubscribe implements Observable.OnSubscribe<Void> {

   private final DtlToolbar dtlToolbar;

   public DtlToolbarTransactinoClicksOnSubscribe(DtlToolbar dtlToolbar) {
      this.dtlToolbar = dtlToolbar;
   }

   @Override
   public void call(Subscriber<? super Void> subscriber) {
      ExpandableDtlToolbar.TransactionButtonListener transactionButtonListener = () -> {
         if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(null);
         }
      };

      dtlToolbar.addTransactionButtonListener(transactionButtonListener);

      subscriber.add(new MainThreadSubscription() {
         @Override
         protected void onUnsubscribe() {
            dtlToolbar.removeTransactionButtonListener(transactionButtonListener);
         }
      });
   }
}

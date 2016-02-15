package com.worldventures.dreamtrips.core.rx.goro;

import com.stanfy.enroscar.goro.FutureObserver;
import com.stanfy.enroscar.goro.Goro;
import com.stanfy.enroscar.goro.ObservableFuture;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Integration point for RxJava.
 * @author Roman Mazur - Stanfy (http://stanfy.com)
 */
public class RxGoro {

  /** Goro instance. */
  private final Goro goro;

  public RxGoro(final Goro goro) {
    this.goro = goro;
  }

  /** @return wrapped {@link Goro} instance */
  public Goro wrappedGoro() { return goro; }

  /**
   * @see Goro#schedule(Callable)
   */
  public <T> Observable<T> schedule(final Callable<T> task) {
    return schedule(Goro.DEFAULT_QUEUE, task);
  }

  /**
   * @see Goro#schedule(String, Callable)
   */
  public <T> Observable<T> schedule(final String queue, final Callable<T> task) {
    return Observable.create(new Observable.OnSubscribe<T>() {
      @Override
      public void call(final Subscriber<? super T> subscriber) {
        ObservableFuture<T> future = goro.schedule(queue, task);
        subscriber.add(new Subscription() {
          @Override
          public void unsubscribe() {
            future.cancel(true);
          }

          @Override
          public boolean isUnsubscribed() {
            return future.isCancelled();
          }
        });

        future.subscribe(new FutureObserver<T>() {
          @Override
          public void onSuccess(T value) {
            subscriber.onNext(value);
            subscriber.onCompleted();
          }

          @Override
          public void onError(Throwable error) {
            subscriber.onError(error);
          }
        });
      }
    });
  }

}

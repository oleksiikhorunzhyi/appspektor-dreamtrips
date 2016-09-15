package com.worldventures.dreamtrips.core.rx.composer;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IoToMainComposer<T> implements Observable.Transformer<T, T> {

   @Override
   public Observable<T> call(Observable<T> observable) {
      return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
   }
}

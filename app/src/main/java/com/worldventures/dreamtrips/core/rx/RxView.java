package com.worldventures.dreamtrips.core.rx;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import rx.Observable;

public interface RxView extends Presenter.View {

   /**
    * Use bindUntilDropView()
    *
    * @param observable
    * @param <T>
    * @return
    * @see RxView#bindUntilDropView(rx.Observable)
    */
   @Deprecated
   <T> Observable<T> bind(Observable<T> observable);

   <T> Observable<T> bindUntilStop(Observable<T> observable);

   <T> Observable<T> bindUntilDropView(Observable<T> observable);
}

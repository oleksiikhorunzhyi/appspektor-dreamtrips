package com.worldventures.dreamtrips.core.rx;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import rx.Observable;

public interface RxView extends Presenter.View {

   <T> Observable<T> bind(Observable<T> observable);

}

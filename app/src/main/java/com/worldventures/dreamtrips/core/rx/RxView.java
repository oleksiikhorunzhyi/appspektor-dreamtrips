package com.worldventures.dreamtrips.core.rx;

import com.trello.rxlifecycle.FragmentEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import rx.Observable;

public interface RxView extends Presenter.View {

    Observable<FragmentEvent> lifecycle();

}

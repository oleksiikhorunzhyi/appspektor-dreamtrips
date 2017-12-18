package com.worldventures.wallet.ui.common.base.screen;

import rx.Observable;

public interface RxLifecycleView {

   <T> Observable.Transformer<T, T> bindUntilDetach();
}

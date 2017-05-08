package com.worldventures.dreamtrips.wallet.ui.common.base.screen;

import rx.Observable;

public interface RxLifecycleView {

   <T> Observable.Transformer<T, T> lifecycle();
}

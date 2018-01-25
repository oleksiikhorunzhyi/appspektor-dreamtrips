package com.worldventures.wallet.ui.common.base.screen

import rx.Observable

interface LifecycleHolder {

   fun detachObservable(): Observable<Void>
}

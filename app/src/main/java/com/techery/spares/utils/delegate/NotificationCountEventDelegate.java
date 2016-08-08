package com.techery.spares.utils.delegate;

import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.delegate.EventDelegate;

import rx.Observable;

public class NotificationCountEventDelegate extends EventDelegate {

    @Override
    public Observable getObservable() {
        return super.getObservable().compose(new IoToMainComposer<>());
    }
}

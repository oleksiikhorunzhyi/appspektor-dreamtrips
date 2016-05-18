package com.messenger.util.serverfacade;

import com.messenger.delegate.chat.flagging.FlagMessageDTO;
import com.messenger.messengerservers.loaders.FlagMessageLoader;

import rx.Observable;

public class MockFlagMessageLoader implements FlagMessageLoader {

    private Observable.OnSubscribe<FlagMessageDTO> onSubscribe;

    public MockFlagMessageLoader(Observable.OnSubscribe<FlagMessageDTO> onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    @Override
    public Observable<FlagMessageDTO> flagMessage(FlagMessageDTO flagMessageDTO) {
        return Observable.create(onSubscribe);
    }
}

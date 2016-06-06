package com.messenger.messengerservers.loaders;

import com.messenger.delegate.chat.flagging.FlagMessageDTO;

import rx.Observable;

public interface FlagMessageLoader {

    Observable<FlagMessageDTO> flagMessage(FlagMessageDTO flagMessageDTO);

}

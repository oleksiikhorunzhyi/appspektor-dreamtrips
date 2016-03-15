package com.messenger.util;


import com.techery.spares.session.SessionHolder;

public final class SessionHolderHelper {

    private SessionHolderHelper(){
    }

    public static boolean hasEntity(SessionHolder sessionHolder){
        return sessionHolder.get() != null && sessionHolder.get().isPresent()
                && sessionHolder.get().get() != null;
    }
}

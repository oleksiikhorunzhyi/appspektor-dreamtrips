package com.worldventures.dreamtrips.util;

import rx.functions.Func0;

public class Preconditions {

    private Preconditions() {}

    static public void check(Func0<Boolean> predicate, String failMessage) throws IllegalStateException {
        if (!predicate.call()) throw new IllegalStateException(failMessage);
    }

}

package com.techery.spares.utils;

import rx.Observable;

public interface UserStatusAdapter {

   Observable<Boolean> getUserHolder(String username);
}

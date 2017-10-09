package com.worldventures.dreamtrips.social.util;

import rx.Observable;

public interface UserStatusAdapter {

   Observable<Boolean> getUserHolder(String username);
}

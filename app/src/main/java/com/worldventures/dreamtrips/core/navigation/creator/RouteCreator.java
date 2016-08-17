package com.worldventures.dreamtrips.core.navigation.creator;

import com.worldventures.dreamtrips.core.navigation.Route;

public interface RouteCreator<T> {

   Route createRoute(T arg);

}


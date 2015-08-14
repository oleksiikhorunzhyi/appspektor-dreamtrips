package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;

public interface Navigator {

    void move(Route route);

    void move(Route route, Bundle bundle);

    void attach(Route route);

    void attach(Route route, Bundle bundle);

}

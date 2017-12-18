package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public interface Navigator {
   void move(Class<? extends Fragment> clazzName, Bundle bundle);
}

package com.worldventures.dreamtrips.core.navigation.creator;

import android.support.v4.app.Fragment;

public interface FragmentClassProvider<T> {

   Class<? extends Fragment> provideFragmentClass(T arg);
}


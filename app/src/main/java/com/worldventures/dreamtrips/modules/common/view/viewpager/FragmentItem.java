package com.worldventures.dreamtrips.modules.common.view.viewpager;

import android.support.v4.app.Fragment;

public class FragmentItem {
    public final Class<? extends Fragment> aClass;
    public final String title;

    public FragmentItem(Class<? extends Fragment> aClass, String title) {
        this.aClass = aClass;
        this.title = title;
    }
}
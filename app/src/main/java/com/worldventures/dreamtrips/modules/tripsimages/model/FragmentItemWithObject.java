package com.worldventures.dreamtrips.modules.tripsimages.model;


import android.support.v4.app.Fragment;

import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;

public class FragmentItemWithObject<T> extends FragmentItem {
    private T object;

    public FragmentItemWithObject(Class<? extends Fragment> aClass, String title, T object) {
        super(aClass, title);
        this.object = object;
    }

    public T getObject() {
        return object;
    }
}
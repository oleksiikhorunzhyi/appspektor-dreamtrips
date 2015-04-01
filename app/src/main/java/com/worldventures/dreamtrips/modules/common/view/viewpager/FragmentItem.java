package com.worldventures.dreamtrips.modules.common.view.viewpager;

public class FragmentItem<T> {
    protected Class<? extends T> aClass;
    protected String title;

    public FragmentItem(Class<? extends T> aClass, String title) {
        this.aClass = aClass;
        this.title = title;
    }
}
package com.worldventures.dreamtrips.view.adapter.viewpager;

public class FragmentItem<T> {
        Class<? extends T> aClass;
        String title;

        public FragmentItem(Class<? extends T> aClass, String title) {
            this.aClass = aClass;
            this.title = title;
        }
    }
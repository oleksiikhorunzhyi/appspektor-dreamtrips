package com.worldventures.dreamtrips.core.navigation;

import com.worldventures.dreamtrips.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.view.fragment.LoginFragment;

import java.util.Comparator;

public enum State {
    LOGIN(LoginFragment.class, "Log in");
    private Class<? extends BaseFragment> fragmentClass;
    private String title;
    private int menuWeight;
    private int imageID;

    State(Class<? extends BaseFragment> fragmentClass, String title) {
        this(fragmentClass, title, -1, -1);
    }

    State(Class<? extends BaseFragment> fragmentClass, String title, int position, int imageID) {
        this.fragmentClass = fragmentClass;
        this.title = title;
        this.menuWeight = position;
        this.imageID = imageID;
    }

    public String getClazzName() {
        return fragmentClass.getName();
    }

    public String getTitle() {
        return title;
    }

    public int getImageID() {
        return imageID;
    }

    public int getMenuWeight() {
        return menuWeight;
    }

    static class MenuComparator implements Comparator<State> {
        public int compare(State strA, State strB) {
            return strA.getMenuWeight() - strB.getMenuWeight();
        }
    }
}
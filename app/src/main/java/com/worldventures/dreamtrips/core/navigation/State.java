package com.worldventures.dreamtrips.core.navigation;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.view.fragment.DreamTripsFragment;
import com.worldventures.dreamtrips.view.fragment.LoginFragment;
import com.worldventures.dreamtrips.view.fragment.ProfileFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesTabsFragment;
import com.worldventures.dreamtrips.view.fragment.WebViewFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum State {
    LOGIN(LoginFragment.class, "Log in"),
    WEB_STATIC(WebViewFragment.class, "Web"),

    DREAMTRIPS(DreamTripsFragment.class, "DreamTrips", 0, R.drawable.ic_dreamtrips),
    TRIP_IMAGES(TripImagesTabsFragment.class, "Trip Images", 1, R.drawable.ic_trip_images),
    MEMBERSHIP(DreamTripsFragment.class, "Membership", 2, R.drawable.ic_membership),
    BUCKET_LIST(DreamTripsFragment.class, "Bucket list", 3, R.drawable.ic_bucket_lists),
    MY_PROFILE(ProfileFragment.class, "My profile", 4, R.drawable.ic_profile),
    FAQ(WebViewFragment.class, "FAQ", 5, R.drawable.ic_faq),
    TERMS_AND_CONDITIONS(WebViewFragment.class, "Terms&Conditions", 6, R.drawable.ic_termsconditions);
    private static ArrayList<State> menuItemsArray;
    private Class<? extends BaseFragment> fragmentClass;
    private String title;
    private int menuWeight;
    private int drawableId;

    State(Class<? extends BaseFragment> fragmentClass, String title) {
        this(fragmentClass, title, -1, -1);
    }

    State(Class<? extends BaseFragment> fragmentClass, String title, int weight, int imageID) {
        this.fragmentClass = fragmentClass;
        this.title = title;
        this.menuWeight = weight;
        this.drawableId = imageID;
    }

    private static void generateSideMenuFields() {
        menuItemsArray = new ArrayList<State>();
        List<State> states = Arrays.asList(State.values());
        Collections.sort(states, new MenuComparator());
        for (State v : states) {
            if (v.menuWeight >= 0)
                menuItemsArray.add(v);
        }
    }

    public static ArrayList<State> getMenuItemsArray() {
        generateSideMenuFields();
        return menuItemsArray;
    }

    public static State findByKey(int i) {
        return menuItemsArray.get(i);
    }

    public String getClazzName() {
        return fragmentClass.getName();
    }

    public String getTitle() {
        return title;
    }

    public int getDrawableId() {
        return drawableId;
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
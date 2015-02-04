package com.worldventures.dreamtrips.core.navigation;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.dialog.BookItDialogFragment;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookPhotoFragment;
import com.worldventures.dreamtrips.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.view.fragment.CreatePhotoFragment;
import com.worldventures.dreamtrips.view.fragment.DetailedTripFragment;
import com.worldventures.dreamtrips.view.fragment.DreamTripsFragment;
import com.worldventures.dreamtrips.view.fragment.FragmentMapTripInfo;
import com.worldventures.dreamtrips.view.fragment.LoginFragment;
import com.worldventures.dreamtrips.view.fragment.MapFragment;
import com.worldventures.dreamtrips.view.fragment.MemberShipFragment;
import com.worldventures.dreamtrips.view.fragment.ProfileFragment;
import com.worldventures.dreamtrips.view.fragment.StaticInfoFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesTabsFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum State {
    LOGIN(LoginFragment.class, "Log in"),
    WEB_STATIC(StaticInfoFragment.class, "Web"),
    CREATE_PHOTO(CreatePhotoFragment.class, "New Photo"),
    DETAILED_TRIP(DetailedTripFragment.class, "Detailed Trip"),
    PICK_FB_ALBUM(FacebookAlbumFragment.class, "Select album"),
    PICK_FB_PHOTO(FacebookPhotoFragment.class, "Select photo"),
    MAP(MapFragment.class, ""),
    MAP_INFO(FragmentMapTripInfo.class, ""),
    BOOK_IT(StaticInfoFragment.BookItFragment.class, ""),
    DREAMTRIPS(DreamTripsFragment.class, "DreamTrips", 0, R.drawable.ic_dreamtrips, 0),
    TRIP_IMAGES(TripImagesTabsFragment.class, "Trip Images", 1, R.drawable.ic_trip_images, 1),
    MEMBERSHIP(MemberShipFragment.class, "Membership", 2, R.drawable.ic_membership, 2),
    BUCKET_LIST(BucketTabsFragment.class, "Bucket list", 3, R.drawable.ic_bucket_lists, 3),
    MY_PROFILE(ProfileFragment.class, "My profile", 4, R.drawable.ic_profile, 4),
    TERMS_OF_SERVICE(StaticInfoFragment.TermsOfServiceFragment.class, "Terms of Service", 5, R.drawable.ic_terms, 5),
    FAQ(StaticInfoFragment.TermsOfServiceFragment.class, "FAQ", 5, R.drawable.ic_faq, 6),
    PRIVACY_POLICY(StaticInfoFragment.PrivacyPolicyFragment.class, "Privacy Policy", 5, R.drawable.ic_termsconditions, 7),
    COOKIE_POLICY(StaticInfoFragment.CookiePolicyFragment.class, "Cookie Policy", 5, R.drawable.ic_cookie, 8);

    private static ArrayList<State> menuItemsArray;
    private Class<? extends BaseFragment> fragmentClass;
    private String title;
    private int menuWeight;
    private int drawableId;
    private int position;

    static {
        generateSideMenuFields();
    }

    State(Class<? extends BaseFragment> fragmentClass, String title) {
        this(fragmentClass, title, -1, -1, -1);
    }

    State(Class<? extends BaseFragment> fragmentClass, String title, int weight, int imageID, int position) {
        this.fragmentClass = fragmentClass;
        this.title = title;
        this.menuWeight = weight;
        this.drawableId = imageID;
        this.position = position;
    }

    private static void generateSideMenuFields() {
        menuItemsArray = new ArrayList<>();
        List<State> states = Arrays.asList(State.values());
        Collections.sort(states, new MenuComparator());
        for (State v : states) {
            if (v.menuWeight >= 0)
                menuItemsArray.add(v);
        }
    }

    public static State restoreByClass(String clazzName) {
        State result = State.DREAMTRIPS;
        for (State state : values()) {
            if (state.getClazzName().equals(clazzName)) {
                result = state;
                break;
            }
        }
        return result;
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

    public int getPosition() {
        return position;
    }
}
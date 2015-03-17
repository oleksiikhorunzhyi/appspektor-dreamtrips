package com.worldventures.dreamtrips.core.navigation;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookPhotoFragment;
import com.worldventures.dreamtrips.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.view.fragment.BucketListPopuralFragment;
import com.worldventures.dreamtrips.view.fragment.BucketListQuickInputFragment;
import com.worldventures.dreamtrips.view.fragment.BucketPopularTabsFragment;
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
import com.worldventures.dreamtrips.view.fragment.reptools.RepToolsFragment;
import com.worldventures.dreamtrips.view.fragment.reptools.SuccessStoriesDetailsFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum State {
    LOGIN(LoginFragment.class, R.string.log_in),
    WEB_STATIC(StaticInfoFragment.class, R.string.web_title),
    CREATE_PHOTO(CreatePhotoFragment.class, R.string.new_photo),
    DETAILED_TRIP(DetailedTripFragment.class, R.string.detailed_trip),
    PICK_FB_ALBUM(FacebookAlbumFragment.class, R.string.fab_select_album),
    PICK_FB_PHOTO(FacebookPhotoFragment.class, R.string.fab_select_photo),
    MAP_INFO(FragmentMapTripInfo.class, 0),
    BOOK_IT(StaticInfoFragment.BookIt.class, R.string.title_book_it),
    SUCCESS_STORES_DETAILS(SuccessStoriesDetailsFragment.class, R.string.success_stories),
    BUNDLE_URL_WEB(StaticInfoFragment.BundleUrlFragment.class, R.string.title_book_it),
    QUICK_INPUT(BucketListQuickInputFragment.class, R.string.bucket_list_my_title),
    POPULAR_BUCKET(BucketListPopuralFragment.class, R.string.bucket_list_my_title),
    POPULAR_TAB_BUCKER(BucketPopularTabsFragment.class, R.string.bucket_list_my_title),
    ENROLL(StaticInfoFragment.EnrollFragment.class, R.string.membership, -1, R.drawable.ic_membership, 2),
    MAP(MapFragment.class, R.string.title_activity_main, -1, R.drawable.ic_dreamtrips, 0),
    DREAMTRIPS(DreamTripsFragment.class, R.string.title_activity_main, 0, R.drawable.ic_dreamtrips, 0),
    TRIP_IMAGES(TripImagesTabsFragment.class, R.string.trip_images, 1, R.drawable.ic_trip_images, 1),
    MEMBERSHIP(MemberShipFragment.class, R.string.membership, 2, R.drawable.ic_membership, 2),
    BUCKET_LIST(BucketTabsFragment.class, R.string.bucket_list, 3, R.drawable.ic_bucket_lists, 3),
    MY_PROFILE(ProfileFragment.class, R.string.my_profile, 4, R.drawable.ic_profile, 4),
    REP_TOOLS(RepToolsFragment.class, R.string.rep_tools, 5, R.drawable.ic_rep_tools, 5),
    TERMS_OF_SERVICE(StaticInfoFragment.TermsOfServiceFragment.class, R.string.terms_of_service, 6, R.drawable.ic_terms, 6),
    FAQ(StaticInfoFragment.FAQFragment.class, R.string.faq, 6, R.drawable.ic_faq, 7),
    PRIVACY_POLICY(StaticInfoFragment.PrivacyPolicyFragment.class, R.string.privacy, 6, R.drawable.ic_termsconditions, 8),
    COOKIE_POLICY(StaticInfoFragment.CookiePolicyFragment.class, R.string.cookie, 6, R.drawable.ic_cookie, 9);

    private static ArrayList<State> menuItemsArray;
    private Class<? extends BaseFragment> fragmentClass;
    private int titleRes;
    private int menuWeight;
    private int drawableId;
    private int position;

    static {
        generateSideMenuFields();
    }

    State(Class<? extends BaseFragment> fragmentClass, int title) {
        this(fragmentClass, title, -1, -1, -1);
    }

    State(Class<? extends BaseFragment> fragmentClass, int title, int weight, int imageID, int position) {
        this.fragmentClass = fragmentClass;
        this.titleRes = title;
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

    public int getTitle() {
        return titleRes;
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
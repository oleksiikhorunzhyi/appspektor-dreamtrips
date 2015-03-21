package com.worldventures.dreamtrips.core.navigation;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookPhotoFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListPopuralFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListQuickInputFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketPopularTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.CreatePhotoFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.DetailedTripFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.DreamTripsFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FragmentMapTripInfo;
import com.worldventures.dreamtrips.modules.auth.view.LoginFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.MapFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.MemberShipFragment;
import com.worldventures.dreamtrips.modules.profile.view.fragment.ProfileFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesTabsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.RepToolsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoriesDetailsFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum Route {
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
    POPULAR_BUCKET(BucketListPopuralFragment.class, R.string.bucket_list_location_popular),
    POPULAR_TAB_BUCKER(BucketPopularTabsFragment.class, R.string.bucket_list_location_popular),
    MAP(MapFragment.class, R.string.title_activity_main),
    ENROLL(StaticInfoFragment.EnrollFragment.class, R.string.membership),
    DREAMTRIPS(DreamTripsFragment.class, R.string.title_activity_main, R.drawable.ic_dreamtrips, 0),
    OTA(OtaFragment.class, R.string.other_travel, R.drawable.ic_other_travel, 1),
    TRIP_IMAGES(TripImagesTabsFragment.class, R.string.trip_images, R.drawable.ic_trip_images, 2),
    MEMBERSHIP(MemberShipFragment.class, R.string.membership, R.drawable.ic_membership, 3),
    BUCKET_LIST(BucketTabsFragment.class, R.string.bucket_list, R.drawable.ic_bucket_lists, 4),
    MY_PROFILE(ProfileFragment.class, R.string.my_profile, R.drawable.ic_profile, 5),
    REP_TOOLS(RepToolsFragment.class, R.string.rep_tools, R.drawable.ic_rep_tools, 6),
    TERMS_OF_SERVICE(StaticInfoFragment.TermsOfServiceFragment.class, R.string.terms_of_service, R.drawable.ic_terms, 7),
    FAQ(StaticInfoFragment.FAQFragment.class, R.string.faq, R.drawable.ic_faq, 8),
    PRIVACY_POLICY(StaticInfoFragment.PrivacyPolicyFragment.class, R.string.privacy, R.drawable.ic_termsconditions, 9),
    COOKIE_POLICY(StaticInfoFragment.CookiePolicyFragment.class, R.string.cookie, R.drawable.ic_cookie, 10);

    private static ArrayList<Route> menuItemsArray = new ArrayList<>();
    private Class<? extends BaseFragment> fragmentClass;
    private int titleRes;
    private int drawableId;
    private int position;

    static {
        generateSideMenuFields();
    }

    Route(Class<? extends BaseFragment> fragmentClass, int title) {
        this(fragmentClass, title, -1, -1);
    }

    Route(Class<? extends BaseFragment> fragmentClass, int title, int imageID, int position) {
        this.fragmentClass = fragmentClass;
        this.titleRes = title;
        this.drawableId = imageID;
        this.position = position;
    }

    private static void generateSideMenuFields() {
        List<Route> routes = Arrays.asList(Route.values());
        Collections.sort(routes, new MenuComparator());
        for (Route v : routes) {
            if (v.position >= 0)
                menuItemsArray.add(v);
        }
    }

    public static Route restoreByClass(String clazzName) {
        Route result = Route.DREAMTRIPS;
        for (Route route : values()) {
            if (route.getClazzName().equals(clazzName)) {
                result = route;
                break;
            }
        }
        return result;
    }

    public static ArrayList<Route> getMenuItemsArray() {
        return menuItemsArray;
    }

    public static Route findByKey(int i) {
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

    public int getPosition() {
        return position;
    }

    static class MenuComparator implements Comparator<Route> {
        public int compare(Route strA, Route strB) {
            return strA.getPosition() - strB.getPosition();
        }
    }
}
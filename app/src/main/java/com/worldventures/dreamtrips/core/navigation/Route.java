package com.worldventures.dreamtrips.core.navigation;


import android.support.annotation.StringRes;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.auth.view.LoginFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketDetailsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketItemEditFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListPopularFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketPopularTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketDetailsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookPhotoFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CommentsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.NotificationFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.PostFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendPreferenceFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendSearchFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendsMainFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.RequestsFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.UsersLikedItemFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.TermsTabFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.EditTemplateFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.MembershipFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.PreviewTemplateFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.SelectTemplateFragment;
import com.worldventures.dreamtrips.modules.profile.view.fragment.AccountFragment;
import com.worldventures.dreamtrips.modules.profile.view.fragment.UserFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.RepToolsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoryDetailsFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripDetailsFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripListFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapInfoFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.AccountImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.CreatePhotoFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenPhotoWrapperFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.PhotoEditFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesTabsFragment;

public enum Route {
    LOGIN(LoginFragment.class),
    WEB_STATIC(StaticInfoFragment.class),
    CREATE_PHOTO(CreatePhotoFragment.class),
    DETAILED_TRIP(TripDetailsFragment.class),
    PICK_FB_ALBUM(FacebookAlbumFragment.class),
    PICK_FB_PHOTO(FacebookPhotoFragment.class),
    MAP_INFO(TripMapInfoFragment.class),
    BOOK_IT(StaticInfoFragment.BookItFragment.class, R.string.book_it),
    SUCCESS_STORES_DETAILS(SuccessStoryDetailsFragment.class),
    BUNDLE_URL_WEB(StaticInfoFragment.BundleUrlFragment.class),

    BUCKET_EDIT(BucketItemEditFragment.class, R.string.bucket_list_edit_header),
    POPULAR_BUCKET(BucketListPopularFragment.class),
    POPULAR_TAB_BUCKER(BucketPopularTabsFragment.class, R.string.bucket_list_location_popular),
    DETAIL_BUCKET(BucketDetailsFragment.class),
    DETAIL_FOREIGN_BUCKET(ForeignBucketDetailsFragment.class),

    MAP(TripMapFragment.class),
    ENROLL(StaticInfoFragment.EnrollFragment.class),
    TRIPLIST(TripListFragment.class),
    OTA(OtaFragment.class),
    TRIP_IMAGES(TripImagesTabsFragment.class, R.string.trip_images),
    FOREIGN_TRIP_IMAGES(TripImagesListFragment.class, R.string.trip_images),
    ACCOUNT_IMAGES(AccountImagesListFragment.class, R.string.trip_images),
    MEMBERSHIP(MembershipFragment.class),
    SELECT_INVITE_TEMPLATE(SelectTemplateFragment.class),
    EDIT_INVITE_TEMPLATE(EditTemplateFragment.class, R.string.title_edit_template),
    BUCKET_LIST(BucketTabsFragment.class, R.string.bucket_list),
    FOREIGN_BUCKET_LIST(ForeignBucketTabsFragment.class, R.string.bucket_list),
    ACCOUNT_PROFILE(AccountFragment.class),
    FOREIGN_PROFILE(UserFragment.class),
    REP_TOOLS(RepToolsFragment.class),
    FAQ(StaticInfoFragment.FAQFragment.class),
    TERMS(TermsTabFragment.class),
    TERMS_OF_SERVICE(StaticInfoFragment.TermsOfServiceFragment.class),
    PRIVACY_POLICY(StaticInfoFragment.PrivacyPolicyFragment.class),
    COOKIE_POLICY(StaticInfoFragment.CookiePolicyFragment.class),
    PREVIEW_TEMPLATE(PreviewTemplateFragment.class),
    COMMENTS(CommentsFragment.class, R.string.comments_title),
    POST_CREATE(PostFragment.class),
    PHOTO_EDIT(PhotoEditFragment.class),
    FRIEND_SEARCH(FriendSearchFragment.class),
    FRIENDS(FriendsMainFragment.class, R.string.profile_friends),
    FRIEND_REQUESTS(RequestsFragment.class, R.string.social_requests),
    FRIEND_PREFERENCES(FriendPreferenceFragment.class, R.string.friend_pref_lists_header),
    FEED(FeedFragment.class, R.string.feed_title),
    NOTIFICATIONS(NotificationFragment.class, R.string.notifications_title),
    SHARE(ShareFragment.class, R.string.feed_title),
    USERS_LIKED_CONTENT(UsersLikedItemFragment.class, R.string.users_who_liked_title),
    FULLSCREEN_PHOTO_LIST(FullScreenPhotoWrapperFragment.class, R.string.empty);

    private Class<? extends BaseFragment> fragmentClass;
    @StringRes
    private int titleRes;

    Route(Class<? extends BaseFragment> fragmentClass) {
        this.fragmentClass = fragmentClass;
    }

    Route(Class<? extends BaseFragment> fragmentClass, @StringRes int titleRes) {
        this(fragmentClass);
        this.titleRes = titleRes;
    }

    public static Route restoreByKey(String key) {
        Route result = Queryable.from(values()).firstOrDefault(route ->
                route.name().equalsIgnoreCase(key));
        return result != null ? result : TRIPLIST;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public String getClazzName() {
        return fragmentClass.getName();
    }
}
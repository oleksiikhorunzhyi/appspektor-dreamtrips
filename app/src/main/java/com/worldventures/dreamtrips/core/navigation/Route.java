package com.worldventures.dreamtrips.core.navigation;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.ui.fragment.MessageImageFullscreenFragment;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImageFragment;
import com.worldventures.dreamtrips.modules.auth.view.LoginFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketDetailsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketItemEditFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListPopularFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketPopularTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketDetailsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.DtGalleryFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.MediaPickerFragment;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DtlPointsEstimationFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlFiltersFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlImageFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlLocationsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlLocationsSearchFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMapFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMapInfoFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantDetailsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantsHostFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantsListFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantsTabsFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanQrCodeFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanReceiptFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlStartFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlTransactionSucceedFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlVerifyAmountFragment;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookPhotoFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CommentableFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CreateFeedPostFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.EditCommentFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.EditPhotoFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.EditPostFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedEntityDetailsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedItemAdditionalInfoFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedItemDetailsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedListAdditionalInfoFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.LocationFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.NotificationFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendListFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendPreferenceFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendSearchFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendsMainFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.MutualFriendsFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.RequestsFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.UsersLikedItemFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.SendFeedbackFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.TermsTabFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.EditTemplateFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.InviteFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.MembershipFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.PreviewTemplateFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.SelectTemplateFragment;
import com.worldventures.dreamtrips.modules.profile.view.fragment.AccountFragment;
import com.worldventures.dreamtrips.modules.profile.view.fragment.UserFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.RepToolsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoryDetailsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoryListFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.TrainingVideosFragment;
import com.worldventures.dreamtrips.modules.settings.view.fragment.GeneralSettingsFragment;
import com.worldventures.dreamtrips.modules.settings.view.fragment.NotificationsSettingsFragment;
import com.worldventures.dreamtrips.modules.settings.view.fragment.SettingsGroupFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FiltersFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripDetailsFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripListFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.AccountImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.CreateTripImageFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.EditPhotoTagsFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenPhotoWrapperFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.MemberImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagePagerFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesTabsFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.BucketPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.InspirePhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.SocialImageFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.TripPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.YSBHPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.video.view.PresentationVideosFragment;
import com.worldventures.dreamtrips.modules.video.view.ThreeSixtyVideosFragment;

public enum Route {
    LOGIN(LoginFragment.class),
    WEB_STATIC(StaticInfoFragment.class),
    DETAILED_TRIP(TripDetailsFragment.class),
    PICK_FB_ALBUM(FacebookAlbumFragment.class),
    PICK_FB_PHOTO(FacebookPhotoFragment.class),
    MAP_INFO(TripMapListFragment.class),
    BOOK_IT(StaticInfoFragment.BookItFragment.class, R.string.book_it),
    SUCCESS_STORES_DETAILS(SuccessStoryDetailsFragment.class),
    BUNDLE_URL_WEB(StaticInfoFragment.BundleUrlFragment.class),

    BUCKET_EDIT(BucketItemEditFragment.class, R.string.bucket_list_edit_header),
    POPULAR_BUCKET(BucketListPopularFragment.class),
    BUCKET_LIST(BucketListFragment.class),
    FOREIGN_BUCKET_LIST(ForeignBucketListFragment.class),
    POPULAR_TAB_BUCKER(BucketPopularTabsFragment.class, R.string.bucket_list_location_popular),
    DETAIL_BUCKET(BucketDetailsFragment.class),
    DETAIL_FOREIGN_BUCKET(ForeignBucketDetailsFragment.class),

    MAP(TripMapFragment.class),
    TRIPLIST(TripListFragment.class),
    TRIP_FILTERS(FiltersFragment.class),
    TRIP_IMAGES_PAGER(TripImagePagerFragment.class),
    OTA(OtaFragment.class),
    TRIP_TAB_IMAGES(TripImagesTabsFragment.class, R.string.trip_images),
    TRIP_LIST_IMAGES(TripImagesListFragment.class, R.string.trip_images),
    ACCOUNT_IMAGES(AccountImagesListFragment.class, R.string.trip_images),
    USER_IMAGES(MemberImagesListFragment.class),
    BASE_IMAGES(BaseImageFragment.class),

    MEMBERSHIP(MembershipFragment.class),
    TRAINING_VIDEOS(TrainingVideosFragment.class),
    ENROLL_MEMBER(StaticInfoFragment.EnrollMemberFragment.class),
    ENROLL_MERCHANT(StaticInfoFragment.EnrollMerchantFragment.class, R.string.suggest_merchant_title),
    SELECT_INVITE_TEMPLATE(SelectTemplateFragment.class, R.string.invitation_template),
    INVITE(InviteFragment.class),
    EDIT_INVITE_TEMPLATE(EditTemplateFragment.class, R.string.title_edit_template),

    BUCKET_TABS(BucketTabsFragment.class, R.string.bucket_list),
    FOREIGN_BUCKET_TABS(ForeignBucketTabsFragment.class, R.string.bucket_list),
    ACCOUNT_PROFILE(AccountFragment.class),
    FOREIGN_PROFILE(UserFragment.class),
    REP_TOOLS(RepToolsFragment.class),
    FAQ(StaticInfoFragment.FAQFragment.class),
    TERMS(TermsTabFragment.class),
    TERMS_OF_SERVICE(StaticInfoFragment.TermsOfServiceFragment.class),
    PRIVACY_POLICY(StaticInfoFragment.PrivacyPolicyFragment.class),
    COOKIE_POLICY(StaticInfoFragment.CookiePolicyFragment.class),
    PREVIEW_TEMPLATE(PreviewTemplateFragment.class),
    COMMENTS(CommentableFragment.class, R.string.comments_title),
    EDIT_COMMENT(EditCommentFragment.class, R.string.empty),
    POST_CREATE(CreateFeedPostFragment.class),
    PHOTO_CREATE(CreateTripImageFragment.class),
    EDIT_POST(EditPostFragment.class),
    EDIT_PHOTO(EditPhotoFragment.class),
    ADD_LOCATION(LocationFragment.class),
    EDIT_PHOTO_TAG_FRAGMENT(EditPhotoTagsFragment.class),
    FRIEND_SEARCH(FriendSearchFragment.class),
    FRIENDS(FriendsMainFragment.class, R.string.profile_friends),
    FRIEND_LIST(FriendListFragment.class),
    FRIEND_REQUESTS(RequestsFragment.class, R.string.social_requests),
    FRIEND_PREFERENCES(FriendPreferenceFragment.class, R.string.friend_pref_lists_header),
    FEED(FeedFragment.class, R.string.feed_title),
    NOTIFICATIONS(NotificationFragment.class, R.string.notifications_title),
    SHARE(ShareFragment.class, R.string.action_share),
    USERS_LIKED_CONTENT(UsersLikedItemFragment.class, R.string.users_who_liked_title),
    FULLSCREEN_PHOTO_LIST(FullScreenPhotoWrapperFragment.class, R.string.empty),

    FEED_ITEM_DETAILS(FeedItemDetailsFragment.class, R.string.empty),
    FEED_ENTITY_DETAILS(FeedEntityDetailsFragment.class, R.string.empty),
    FEED_LIST_ADDITIONAL_INFO(FeedListAdditionalInfoFragment.class, R.string.empty),
    FEED_ITEM_ADDITIONAL_INFO(FeedItemAdditionalInfoFragment.class, R.string.empty),

    DTL_START(DtlStartFragment.class),
    DTL_LOCATIONS(DtlLocationsFragment.class, R.string.dtl_locations_title),
    DTL_LOCATIONS_SEARCH(DtlLocationsSearchFragment.class, R.string.dtl_locations_title),
    DTL_TRANSACTION_SUCCEED(DtlTransactionSucceedFragment.class, R.string.dtl_success_title),
    DTL_MERCHANTS_HOLDER(DtlMerchantsHostFragment.class),
    DTL_MERCHANTS_TABS(DtlMerchantsTabsFragment.class),
    DTL_MERCHANTS_LIST(DtlMerchantsListFragment.class),
    DTL_IMAGE(DtlImageFragment.class),
    DTL_POINTS_ESTIMATION(DtlPointsEstimationFragment.class),
    DTL_FILTERS(DtlFiltersFragment.class),
    DTL_MAP(DtlMapFragment.class),
    DTL_MAP_INFO(DtlMapInfoFragment.class),
    DTL_MERCHANT_DETAILS(DtlMerchantDetailsFragment.class),

    DTL_SCAN_RECEIPT(DtlScanReceiptFragment.class, R.string.dtl_enter_amount),
    DTL_SCAN_QR(DtlScanQrCodeFragment.class, R.string.dtl_barcode_title),
    DTL_VERIFY(DtlVerifyAmountFragment.class, R.string.dtl_verify_amount),

    GALLERY(DtGalleryFragment.class),

    MUTUAL_FRIENDS(MutualFriendsFragment.class, R.string.user_mutual_friends),

    ENROLL_REP(StaticInfoFragment.EnrollRepFragment.class),
    SUCCESS_STORY_LIST(SuccessStoryListFragment.class),

    INSPIRE_PHOTO_FULLSCREEN(InspirePhotoFullscreenFragment.class),
    SOCIAL_IMAGE_FULLSCREEN(SocialImageFullscreenFragment.class),
    YSBH_FULLSCREEN(YSBHPhotoFullscreenFragment.class),
    TRIP_PHOTO_FULLSCREEN(TripPhotoFullscreenFragment.class),
    BUCKET_PHOTO_FULLSCREEN(BucketPhotoFullscreenFragment.class),
    MESSAGE_IMAGE_FULLSCREEN(MessageImageFullscreenFragment.class),

    THREE_SIXTY_VIDEOS(ThreeSixtyVideosFragment.class),
    PRESENTATION_VIDEOS(PresentationVideosFragment.class),
    SEND_FEEDBACK(SendFeedbackFragment.class),

    SETTINGS(SettingsGroupFragment.class, R.string.settings),
    SETTINGS_NOTIFICATIONS(NotificationsSettingsFragment.class),
    SETTINGS_GENERAL(GeneralSettingsFragment.class),

    MEDIA_PICKER(MediaPickerFragment.class);

    private Class<? extends Fragment> fragmentClass;
    @StringRes
    private int titleRes;

    Route(Class<? extends Fragment> fragmentClass) {
        this.fragmentClass = fragmentClass;
    }

    Route(Class<? extends Fragment> fragmentClass, @StringRes int titleRes) {
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

    public Class<? extends Fragment> getClazz() {
        return fragmentClass;
    }
}

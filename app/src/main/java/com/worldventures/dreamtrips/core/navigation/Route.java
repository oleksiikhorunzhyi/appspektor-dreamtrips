package com.worldventures.dreamtrips.core.navigation;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.ui.fragment.MessageImageFullscreenFragment;
import com.messenger.ui.fragment.PhotoAttachmentPagerFragment;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketDetailsFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketItemEditFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketListPopularFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketPhotoFullscreenFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketPhotoViewPagerFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketPopularTabsFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.ForeignBucketDetailsFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.ForeignBucketListFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.ForeignBucketTabsFragment;
import com.worldventures.dreamtrips.social.ui.share.view.ShareFragment;
import com.worldventures.dreamtrips.modules.dtl.view.dialog.DtlPointsEstimationFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanQrCodeFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanReceiptFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlTransactionSucceedFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlVerifyAmountFragment;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookPhotoFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.CommentableFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.CreateFeedPostFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.DescriptionCreatorFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.EditCommentFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.EditPhotoFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.EditPostFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedEntityDetailsFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedItemAdditionalInfoFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedItemDetailsFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedListAdditionalInfoFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.HashtagFeedFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.LocationFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.NotificationFragment;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.FriendListFragment;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.FriendPreferenceFragment;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.FriendSearchFragment;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.FriendsMainFragment;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.MutualFriendsFragment;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.RequestsFragment;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.UsersLikedItemFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.FeedbackImageAttachmentFullscreenFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.FeedbackImageAttachmentsFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.HelpDocumentListFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.HelpFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.LegalTermsFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.SendFeedbackFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.DocumentFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.EnrollMemberFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.EnrollMerchantFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.EnrollRepFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.WVAdvantageFragment;
import com.worldventures.dreamtrips.modules.media_picker.view.fragment.DtGalleryFragment;
import com.worldventures.dreamtrips.modules.media_picker.view.fragment.MediaPickerFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.EditTemplateFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.InviteFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.MembershipFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.PodcastsFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.PreviewTemplateFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.SelectTemplateFragment;
import com.worldventures.dreamtrips.social.ui.profile.view.fragment.AccountFragment;
import com.worldventures.dreamtrips.social.ui.profile.view.fragment.UserFragment;
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.RepToolsFragment;
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.SuccessStoryDetailsFragment;
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.SuccessStoryListFragment;
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.TrainingVideosFragment;
import com.worldventures.dreamtrips.social.ui.settings.view.fragment.GeneralSettingsFragment;
import com.worldventures.dreamtrips.social.ui.settings.view.fragment.NotificationsSettingsFragment;
import com.worldventures.dreamtrips.social.ui.settings.view.fragment.SettingsGroupFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FiltersFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripDetailsFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripImagePagerFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripListFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapListFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripViewPagerFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.EditPhotoTagsFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.FullscreenPhotoFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.FullscreenVideoFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.MemberImagesFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.TripImagesFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.TripImagesFullscreenFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.TripImagesTabFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.inspire_me.FullscreenInspireMeFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.inspire_me.InspireMeFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.inspire_me.InspireMeViewPagerFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh.FullscreenYsbhFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh.YouShouldBeHereFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh.YsbhViewPagerFragment;
import com.worldventures.dreamtrips.social.ui.video.view.HelpVideosFragment;
import com.worldventures.dreamtrips.social.ui.video.view.PresentationVideosFragment;
import com.worldventures.dreamtrips.social.ui.video.view.ThreeSixtyVideosFragment;

public enum Route {
   DETAILED_TRIP(TripDetailsFragment.class),
   PICK_FB_ALBUM(FacebookAlbumFragment.class),
   PICK_FB_PHOTO(FacebookPhotoFragment.class),
   MAP_INFO(TripMapListFragment.class),
   BOOK_IT(StaticInfoFragment.BookItFragment.class, R.string.book_it),
   SUCCESS_STORES_DETAILS(SuccessStoryDetailsFragment.class),

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
   TRIP_IMAGE_PAGER(TripImagePagerFragment.class),
   OTA(OtaFragment.class),

   TRIP_TAB_IMAGES(TripImagesTabFragment.class, R.string.trip_images),
   MEMBERS_IMAGES(MemberImagesFragment.class),
   ACCOUNT_IMAGES(TripImagesFragment.class, R.string.trip_images),
   TRIP_IMAGES_FULLSCREEN(TripImagesFullscreenFragment.class),
   SOCIAL_IMAGE_FULLSCREEN(FullscreenPhotoFragment.class),
   SOCIAL_VIDEO_FULLSCREEN(FullscreenVideoFragment.class),

   YSBH_IMAGES(YouShouldBeHereFragment.class, R.string.trip_images),
   YSBH_PAGER_IMAGES(YsbhViewPagerFragment.class),
   YSBH_FULLSCREEN_IMAGE(FullscreenYsbhFragment.class),

   INSPIRE_ME_IMAGES(InspireMeFragment.class, R.string.trip_images),
   INSPIRE_PAGER_IMAGES(InspireMeViewPagerFragment.class),
   INSPIRE_ME_FULLSCREEN_IMAGES(FullscreenInspireMeFragment.class),

   MEMBERSHIP(MembershipFragment.class),
   TRAINING_VIDEOS(TrainingVideosFragment.class),
   ENROLL_MEMBER(EnrollMemberFragment.class, R.string.enroll_member),
   ENROLL_MERCHANT(EnrollMerchantFragment.class, R.string.dt_local_tools),
   SELECT_INVITE_TEMPLATE(SelectTemplateFragment.class, R.string.invitation_template),
   INVITE(InviteFragment.class),
   EDIT_INVITE_TEMPLATE(EditTemplateFragment.class, R.string.title_edit_template),
   PODCASTS(PodcastsFragment.class, R.string.podcasts),

   BUCKET_TABS(BucketTabsFragment.class, R.string.bucket_list),
   FOREIGN_BUCKET_TABS(ForeignBucketTabsFragment.class, R.string.bucket_list),
   ACCOUNT_PROFILE(AccountFragment.class),
   FOREIGN_PROFILE(UserFragment.class),
   REP_TOOLS(RepToolsFragment.class),
   HELP(HelpFragment.class),
   HELP_DOCUMENTS_LIST(HelpDocumentListFragment.class),
   DOCUMENT(DocumentFragment.class),
   HELP_VIDEOS(HelpVideosFragment.class),
   LEGAL_TERMS(LegalTermsFragment.class),
   PREVIEW_TEMPLATE(PreviewTemplateFragment.class),
   COMMENTS(CommentableFragment.class, R.string.comments_title),
   EDIT_COMMENT(EditCommentFragment.class, R.string.empty),
   POST_CREATE(CreateFeedPostFragment.class),
   PHOTO_CREATION_DESC(DescriptionCreatorFragment.class, R.string.add_text),
   EDIT_POST(EditPostFragment.class),
   EDIT_PHOTO(EditPhotoFragment.class),
   ADD_LOCATION(LocationFragment.class),
   EDIT_PHOTO_TAG_FRAGMENT(EditPhotoTagsFragment.class),
   FRIEND_SEARCH(FriendSearchFragment.class),
   FRIENDS(FriendsMainFragment.class, R.string.profile_friends),
   FRIEND_LIST(FriendListFragment.class),
   FRIEND_REQUESTS(RequestsFragment.class, R.string.social_requests),
   FRIEND_PREFERENCES(FriendPreferenceFragment.class, R.string.friend_pref_lists_header),
   NOTIFICATIONS(NotificationFragment.class, R.string.notifications_title),
   SHARE(ShareFragment.class, R.string.action_share),
   USERS_LIKED_CONTENT(UsersLikedItemFragment.class, R.string.users_who_liked_title),
   FEED(FeedFragment.class, R.string.feed_title),
   FEED_HASHTAG(HashtagFeedFragment.class, R.string.empty),
   FEEDBACK_IMAGE_ATTACHMENTS(FeedbackImageAttachmentsFragment.class, R.string.empty),

   FEED_ITEM_DETAILS(FeedItemDetailsFragment.class, R.string.empty),
   FEED_ENTITY_DETAILS(FeedEntityDetailsFragment.class, R.string.empty),
   FEED_LIST_ADDITIONAL_INFO(FeedListAdditionalInfoFragment.class, R.string.empty),
   FEED_ITEM_ADDITIONAL_INFO(FeedItemAdditionalInfoFragment.class, R.string.empty),

   // <dtl_legasy_routes>
   // TODO :: 4/24/16 re-write the rest of DTL to Flow-powered views
   DTL_TRANSACTION_SUCCEED(DtlTransactionSucceedFragment.class, R.string.dtl_success_title),
   DTL_POINTS_ESTIMATION(DtlPointsEstimationFragment.class),
   DTL_SCAN_RECEIPT(DtlScanReceiptFragment.class, R.string.dtl_scan_receipt_screen_title),
   DTL_SCAN_QR(DtlScanQrCodeFragment.class, R.string.dtl_barcode_title),
   DTL_VERIFY(DtlVerifyAmountFragment.class, R.string.dtl_verify_amount),
   // </dtl_legasy_routes>

   GALLERY(DtGalleryFragment.class),

   MUTUAL_FRIENDS(MutualFriendsFragment.class, R.string.user_mutual_friends),

   ENROLL_REP(EnrollRepFragment.class),
   WV_ADVANTAGE(WVAdvantageFragment.class, R.string.wv_advantage),
   ENROLL_UPGRADE(StaticInfoFragment.EnrollUpgradeFragment.class, R.string.enroll_member),
   SUCCESS_STORY_LIST(SuccessStoryListFragment.class),

   FEEDBACK_FULLSCREEN_IMAGE_ATTACHMENT(FeedbackImageAttachmentFullscreenFragment.class, R.string.empty),

   TRIP_FULLSCREEN_PHOTO_LIST(TripViewPagerFragment.class),
   TRIP_PHOTO_FULLSCREEN(TripPhotoFullscreenFragment.class),

   BUCKET_FULLSCREEN_PHOTO_LIST(BucketPhotoViewPagerFragment.class, R.string.empty),
   BUCKET_PHOTO_FULLSCREEN(BucketPhotoFullscreenFragment.class),

   MESSENGER_FULLSCREEN_IMAGES(PhotoAttachmentPagerFragment.class),
   MESSAGE_IMAGE_FULLSCREEN(MessageImageFullscreenFragment.class),

   THREE_SIXTY_VIDEOS(ThreeSixtyVideosFragment.class),
   PRESENTATION_VIDEOS(PresentationVideosFragment.class),
   SEND_FEEDBACK(SendFeedbackFragment.class),

   SETTINGS(SettingsGroupFragment.class, R.string.settings),
   SETTINGS_NOTIFICATIONS(NotificationsSettingsFragment.class),
   SETTINGS_GENERAL(GeneralSettingsFragment.class),

   MEDIA_PICKER(MediaPickerFragment.class);

   private Class<? extends Fragment> fragmentClass;
   @StringRes private int titleRes;

   Route(Class<? extends Fragment> fragmentClass) {
      this.fragmentClass = fragmentClass;
   }

   Route(Class<? extends Fragment> fragmentClass, @StringRes int titleRes) {
      this(fragmentClass);
      this.titleRes = titleRes;
   }

   public static Route restoreByKey(String key) {
      Route result = Queryable.from(values()).firstOrDefault(route -> route.name().equalsIgnoreCase(key));
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

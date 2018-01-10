package com.worldventures.dreamtrips.social.di;

import android.support.annotation.DrawableRes;

import com.worldventures.core.component.ComponentDescription;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.config.ConfigurationModule;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripListFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapFragment;
import com.worldventures.dreamtrips.social.di.friends.UserAppModule;
import com.worldventures.dreamtrips.social.ui.background_uploading.BackgroundUploadingModule;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.social.ui.feed.FeedAppModule;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.NotificationFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.HelpFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.LegalTermsFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.SendFeedbackFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.MembershipFragment;
import com.worldventures.dreamtrips.social.ui.profile.view.fragment.AccountFragment;
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.RepToolsFragment;
import com.worldventures.dreamtrips.social.ui.settings.view.fragment.SettingsGroupFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.TripImagesTabFragment;

import dagger.Module;
import dagger.Provides;

@Module(
      library = true,
      complete = false,
      includes = {
            BackgroundUploadingModule.class,
            SocialDelegateModule.class,
            SocialActionStorageModule.class,
            SocialInteractorModule.class,
            FeedAppModule.class,
            ConfigurationModule.class,
            EventDelegateModule.class,
            SocialInitializerModule.class,
            SocialMappingModule.class,
            SocialSnappyModule.class,
            UserAppModule.class
      }
)
public class SocialAppModule {

   public static final String FEED = "FEED";
   public static final String NOTIFICATIONS = "NOTIFICATIONS";
   public static final String TRIPS = "TRIPS";
   public static final String MAP_TRIPS = "MAP_TRIPS";
   public static final String OTA = "OTA";
   public static final String TRIP_IMAGES = "TRIP_IMAGES";
   public static final String MEMBERSHIP = "MEMBERSHIP";
   public static final String BUCKETLIST = "BUCKET_TABS";
   public static final String ACCOUNT_PROFILE = "ACCOUNT_PROFILE";
   public static final String HELP = "HELP";
   public static final String TERMS = "LEGAL_TERMS";
   public static final String SETTINGS = "SETTINGS";
   public static final String SEND_FEEDBACK = "SEND_FEEDBACK";
   public static final String REP_TOOLS = "REP_TOOLS";
   public static final String LOGOUT = "Logout";

   public static final String MALAYSIYA_COUNTRY_CODE = "my";

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideTripsComponent(SessionHolder sessionHolder) {
      return new ComponentDescription.Builder()
            .key(TRIPS)
            .toolbarLogo(getLogo(sessionHolder))
            .navMenuTitle(R.string.trips)
            .icon(R.drawable.ic_dreamtrips)
            .fragmentClass(TripListFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideMembershipComponent() {
      return new ComponentDescription.Builder()
            .key(MEMBERSHIP)
            .navMenuTitle(R.string.membership)
            .toolbarTitle(R.string.membership)
            .icon(R.drawable.ic_membership)
            .fragmentClass(MembershipFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideFeedComponent() {
      return new ComponentDescription.Builder()
            .key(FEED)
            .navMenuTitle(R.string.feed_title)
            .toolbarTitle(R.string.feed_title)
            .icon(R.drawable.ic_feed)
            .fragmentClass(FeedFragment.class)
            .shouldFinishMainActivity(true)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideNotificationComponent() {
      return new ComponentDescription.Builder()
            .key(NOTIFICATIONS)
            .navMenuTitle(R.string.notifications_title)
            .toolbarTitle(R.string.notifications_title)
            .icon(R.drawable.ic_notifications)
            .fragmentClass(NotificationFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideTripImagesComponent() {
      return new ComponentDescription.Builder()
            .key(TRIP_IMAGES)
            .navMenuTitle(R.string.trip_images)
            .toolbarTitle(R.string.trip_images)
            .icon(R.drawable.ic_trip_images)
            .fragmentClass(TripImagesTabFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideSettingsComponent() {
      return new ComponentDescription.Builder()
            .key(SETTINGS)
            .navMenuTitle(R.string.settings)
            .toolbarTitle(R.string.settings)
            .icon(R.drawable.ic_settings_menu)
            .fragmentClass(SettingsGroupFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideMapTripsComponent(SessionHolder sessionHolder) {
      return new ComponentDescription.Builder()
            .key(MAP_TRIPS)
            .toolbarLogo(getLogo(sessionHolder))
            .navMenuTitle(R.string.trips)
            .icon(R.drawable.ic_dreamtrips)
            .ignored(true)
            .skipGeneralToolbar(true)
            .fragmentClass(TripMapFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideOTAComponent() {
      return new ComponentDescription.Builder()
            .key(OTA)
            .toolbarTitle(R.string.other_travel)
            .navMenuTitle(R.string.other_travel)
            .icon(R.drawable.ic_other_travel)
            .fragmentClass(OtaFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideBucketListComponent() {
      return new ComponentDescription.Builder()
            .key(BUCKETLIST)
            .navMenuTitle(R.string.bucket_list)
            .toolbarTitle(R.string.bucket_list)
            .icon(R.drawable.ic_bucket_lists)
            .fragmentClass(BucketTabsFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideProfileComponent() {
      return new ComponentDescription.Builder()
            .key(ACCOUNT_PROFILE)
            .navMenuTitle(R.string.my_profile)
            .icon(R.drawable.ic_profile)
            .skipGeneralToolbar(true)
            .fragmentClass(AccountFragment.class)
            .shouldFinishMainActivity(true)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideRepToolsComponent() {
      return new ComponentDescription.Builder()
            .key(REP_TOOLS)
            .navMenuTitle(R.string.rep_tools)
            .toolbarTitle(R.string.rep_tools)
            .icon(R.drawable.ic_rep_tools)
            .fragmentClass(RepToolsFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideTermsOfServiceComponent() {
      return new ComponentDescription.Builder()
            .key(TERMS)
            .navMenuTitle(R.string.legal_terms)
            .toolbarTitle(R.string.legal_terms)
            .icon(R.drawable.ic_termsconditions)
            .fragmentClass(LegalTermsFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideHelpComponent() {
      return new ComponentDescription.Builder()
            .key(HELP)
            .navMenuTitle(R.string.help)
            .toolbarTitle(R.string.help)
            .icon(R.drawable.ic_help_selector)
            .fragmentClass(HelpFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideFeedbackComponent() {
      return new ComponentDescription.Builder()
            .key(SEND_FEEDBACK)
            .navMenuTitle(R.string.send_feedback)
            .toolbarTitle(R.string.send_feedback)
            .icon(R.drawable.ic_send_feedback)
            .fragmentClass(SendFeedbackFragment.class)
            .build();
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideLogoutComponent() {
      return new ComponentDescription.Builder()
            .key(LOGOUT)
            .navMenuTitle(R.string.logout_component)
            .icon(R.drawable.ic_logout)
            .build();
   }

   @DrawableRes
   private int getLogo(SessionHolder sessionHolder) {
      Optional<UserSession> sessionOptional = sessionHolder.get();
      if (sessionOptional.isPresent()) {
         String countryCode = sessionOptional.get().user().getCountryCode();
         if (MALAYSIYA_COUNTRY_CODE.equalsIgnoreCase(countryCode)) {
            return R.drawable.dt_action_bar_logo_skyzone;
         }
      }
      return R.drawable.dt_action_bar_logo;
   }

}

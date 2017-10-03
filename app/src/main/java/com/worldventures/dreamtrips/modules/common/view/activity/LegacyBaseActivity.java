package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.messenger.di.MessengerActivityModule;
import com.worldventures.core.ui.view.activity.BaseActivity;
import com.worldventures.dreamtrips.core.module.LegacyActivityModule;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.config.VersionCheckActivityModule;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlActivityModule;
import com.worldventures.dreamtrips.modules.facebook.FacebookModule;
import com.worldventures.dreamtrips.modules.picklocation.LocationPickerModule;
import com.worldventures.dreamtrips.modules.trips.TripsModule;
import com.worldventures.dreamtrips.social.di.SocialCommonActivityModule;
import com.worldventures.dreamtrips.social.ui.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.social.ui.feed.FeedActivityModule;
import com.worldventures.dreamtrips.social.ui.friends.FriendsModule;
import com.worldventures.dreamtrips.social.ui.infopages.InfoActivityModule;
import com.worldventures.dreamtrips.social.ui.membership.MembershipModule;
import com.worldventures.dreamtrips.social.ui.profile.ProfileActivityModule;
import com.worldventures.dreamtrips.social.ui.reptools.ReptoolsActivityModule;
import com.worldventures.dreamtrips.social.ui.settings.SettingsModule;
import com.worldventures.dreamtrips.social.ui.tripsimages.TripImagesModule;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

public abstract class LegacyBaseActivity extends BaseActivity {

   @Inject protected ActivityResultDelegate activityResultDelegate;
   @Inject protected BackStackDelegate backStackDelegate;
   @Inject protected Router router;
   @Inject protected ActivityRouter activityRouter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      ButterKnife.inject(this);
   }

   @Override
   protected void openLoginActivity() {
      activityRouter.openLaunch();
   }

   @Override
   protected List<Object> getModules() {
      List<Object> modules = super.getModules();
      modules.add(new BucketListModule());
      modules.add(new FacebookModule()); // legacy picker
      modules.add(new InfoActivityModule());
      modules.add(new ProfileActivityModule());
      modules.add(new ReptoolsActivityModule());
      modules.add(new TripsModule());
      modules.add(new TripImagesModule());
      modules.add(new MembershipModule());
      modules.add(new FriendsModule());
      modules.add(new FeedActivityModule());
      modules.add(new SettingsModule());
      modules.add(new MessengerActivityModule());
      modules.add(new DtlActivityModule());
      modules.add(new LocationPickerModule());
      modules.add(new VersionCheckActivityModule());
      modules.add(new SocialCommonActivityModule());
      modules.add(new LegacyActivityModule(this));
      return modules;
   }

   protected boolean handleBackPressed() {
      return backStackDelegate.handleBackPressed() || checkChildFragments(getSupportFragmentManager());
   }

   @Override
   public void onBackPressed() {
      if (handleBackPressed()) return;
      FragmentManager fm = getSupportFragmentManager();

      if (fm.getBackStackEntryCount() > 1) {
         fm.popBackStack();
         onTopLevelBackStackPopped();
      } else {
         finish();
      }
   }

   private boolean checkChildFragments(FragmentManager fragmentManager) {
      if (fragmentManager.getFragments() != null) for (Fragment fragment : fragmentManager.getFragments()) {
         if (fragment != null && fragment.isVisible()) {
            FragmentManager childFm = fragment.getChildFragmentManager();
            if (!checkChildFragments(childFm) && childFm.getBackStackEntryCount() > 0) {
               childFm.popBackStack();
               return true;
            }
         }
      }

      return false;
   }

   protected void onTopLevelBackStackPopped() {
      if (getSupportFragmentManager().getBackStackEntryCount() == 0) finish();
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case android.R.id.home:
            finish();
            return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      activityResultDelegate.onActivityResult(requestCode, resultCode, data);
      super.onActivityResult(requestCode, resultCode, data);
   }
}

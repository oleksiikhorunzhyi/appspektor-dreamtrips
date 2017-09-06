package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.messenger.di.MessengerActivityModule;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.common.delegate.PickImageDelegate;
import com.worldventures.dreamtrips.modules.config.VersionCheckActivityModule;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlActivityModule;
import com.worldventures.dreamtrips.modules.feed.FeedActivityModule;
import com.worldventures.dreamtrips.modules.friends.FriendsModule;
import com.worldventures.dreamtrips.modules.infopages.InfoActivityModule;
import com.worldventures.dreamtrips.modules.membership.MembershipModule;
import com.worldventures.dreamtrips.modules.picklocation.LocationPickerModule;
import com.worldventures.dreamtrips.modules.player.PodcastModule;
import com.worldventures.dreamtrips.modules.profile.ProfileActivityModule;
import com.worldventures.dreamtrips.modules.reptools.ReptoolsActivityModule;
import com.worldventures.dreamtrips.modules.settings.SettingsModule;
import com.worldventures.dreamtrips.modules.trips.TripsModule;
import com.worldventures.dreamtrips.modules.tripsimages.TripImagesModule;

import java.util.List;

import javax.inject.Inject;

public class LegacyBaseActivity extends BaseActivity {

   @Inject protected ActivityResultDelegate activityResultDelegate;
   @Inject protected BackStackDelegate backStackDelegate;
   @Inject protected PickImageDelegate pickImageDelegate;
   @Inject protected Router router;
   @Inject protected ActivityRouter activityRouter;

   @Override
   protected void openLoginActivity() {
      activityRouter.openLaunch();
   }

   @Override
   protected List<Object> getModules() {
      List<Object> modules = super.getModules();
      modules.add(new BucketListModule());
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
      modules.add(new PodcastModule());
      modules.add(new VersionCheckActivityModule());
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
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      pickImageDelegate.saveInstanceState(outState);
   }

   @Override
   protected void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      pickImageDelegate.restoreInstanceState(savedInstanceState);
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
      pickImageDelegate.onActivityResult(requestCode, resultCode, data);
      super.onActivityResult(requestCode, resultCode, data);
   }
}

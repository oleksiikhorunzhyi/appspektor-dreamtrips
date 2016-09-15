package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.messenger.di.MessengerActivityModule;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.activity.InjectingActivity;
import com.worldventures.dreamtrips.core.module.ActivityModule;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.tracksystem.LifecycleEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.MonitoringHelper;
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.common.CommonModule;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlActivityModule;
import com.worldventures.dreamtrips.modules.facebook.FacebookModule;
import com.worldventures.dreamtrips.modules.feed.FeedModule;
import com.worldventures.dreamtrips.modules.friends.FriendsModule;
import com.worldventures.dreamtrips.modules.infopages.InfoModule;
import com.worldventures.dreamtrips.modules.membership.MembershipModule;
import com.worldventures.dreamtrips.modules.picklocation.LocationPickerModule;
import com.worldventures.dreamtrips.modules.player.PodcastModule;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.reptools.ReptoolsModule;
import com.worldventures.dreamtrips.modules.settings.SettingsModule;
import com.worldventures.dreamtrips.modules.trips.TripsModule;
import com.worldventures.dreamtrips.modules.tripsimages.TripsImagesModule;
import com.worldventures.dreamtrips.modules.video.VideoModule;

import java.util.List;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

public abstract class BaseActivity extends InjectingActivity {

   @Inject protected ActivityResultDelegate activityResultDelegate;
   @Inject BackStackDelegate backStackDelegate;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject protected PermissionDispatcher permissionDispatcher;
   @Inject protected Router router;
   @Inject ActivityRouter activityRouter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      MonitoringHelper.setInteractionName(this);
      super.onCreate(savedInstanceState);
      analyticsInteractor.analyticsActionPipe()
            .send(new LifecycleEvent(this, LifecycleEvent.ACTION_ONCREATE), Schedulers.immediate());
   }

   @Override
   protected void onStart() {
      analyticsInteractor.analyticsActionPipe().send(new LifecycleEvent(this, LifecycleEvent.ACTION_ONSTART), Schedulers
            .immediate());
      super.onStart();
   }

   @Override
   protected void onStop() {
      super.onStop();
      analyticsInteractor.analyticsActionPipe()
            .send(new LifecycleEvent(this, LifecycleEvent.ACTION_ONSTOP), Schedulers.immediate());
   }

   @Override
   protected void onResume() {
      super.onResume();
      analyticsInteractor.analyticsActionPipe()
            .send(new LifecycleEvent(this, LifecycleEvent.ACTION_ONRESUME), Schedulers.immediate());
   }

   @Override
   protected void onPause() {
      super.onPause();
      analyticsInteractor.analyticsActionPipe().send(new LifecycleEvent(this, LifecycleEvent.ACTION_ONPAUSE), Schedulers
            .immediate());
   }

   @Override
   public void onBackPressed() {
      if (backStackDelegate.handleBackPressed() || checkChildFragments(getSupportFragmentManager())) return;

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
   protected List<Object> getModules() {
      List<Object> modules = super.getModules();
      modules.add(new ActivityModule(this));
      modules.add(new BucketListModule());
      modules.add(new CommonModule());
      modules.add(new FacebookModule());
      modules.add(new InfoModule());
      modules.add(new ProfileModule());
      modules.add(new ReptoolsModule());
      modules.add(new TripsModule());
      modules.add(new TripsImagesModule());
      modules.add(new VideoModule());
      modules.add(new MembershipModule());
      modules.add(new FriendsModule());
      modules.add(new FeedModule());
      modules.add(new SettingsModule());
      modules.add(new MessengerActivityModule());
      modules.add(new DtlActivityModule());
      modules.add(new LocationPickerModule());
      modules.add(new PodcastModule());
      return modules;
   }

   public void onEvent(SessionHolder.Events.SessionDestroyed sessionDestroyed) {
      activityRouter.openLaunch();
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      activityResultDelegate.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      permissionDispatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
}

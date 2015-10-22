package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.activity.InjectingActivity;
import com.worldventures.dreamtrips.core.module.ActivityModule;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.AuthModule;
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.common.CommonModule;
import com.worldventures.dreamtrips.modules.dtl.DtlModule;
import com.worldventures.dreamtrips.modules.facebook.FacebookModule;
import com.worldventures.dreamtrips.modules.feed.FeedModule;
import com.worldventures.dreamtrips.modules.friends.FriendsModule;
import com.worldventures.dreamtrips.modules.infopages.InfoModule;
import com.worldventures.dreamtrips.modules.membership.MembershipModule;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.reptools.ReptoolsModule;
import com.worldventures.dreamtrips.modules.trips.TripsModule;
import com.worldventures.dreamtrips.modules.tripsimages.TripsImagesModule;
import com.worldventures.dreamtrips.modules.video.VideoModule;

import java.util.List;

import javax.inject.Inject;

public abstract class BaseActivity extends InjectingActivity {

    @Inject
    protected ActivityRouter router;

    @Inject
    protected FragmentCompass fragmentCompass;

    @Inject
    protected ActivityResultDelegate activityResultDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TrackingHelper.onCreate(this);
    }

    @Override
    protected void onStart() {
        TrackingHelper.onStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TrackingHelper.onStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TrackingHelper.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TrackingHelper.onPause(this);
    }

    @Override
    public void onBackPressed() {
        if (!checkChildFragments(getSupportFragmentManager())) {
            super.onBackPressed();
            topLevelBackStackPopped();
        }
    }

    private boolean checkChildFragments(FragmentManager fragmentManager) {
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null && fragment.isVisible()) {
                FragmentManager childFm = fragment.getChildFragmentManager();
                if (childFm.getBackStackEntryCount() > 0) {
                    childFm.popBackStack();
                    return true;
                }
            }
        }
        return false;
    }

    protected void topLevelBackStackPopped() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) finish();
    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new ActivityModule(this));
        modules.add(new AuthModule());
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
        modules.add(new DtlModule());
        return modules;
    }

    public void onEvent(SessionHolder.Events.SessionDestroyed sessionDestroyed) {
        NavigationBuilder.create()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .with(router).move(Route.LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityResultDelegate.onActivityResult(requestCode, resultCode, data);
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


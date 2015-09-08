package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.activity.InjectingActivity;
import com.worldventures.dreamtrips.core.module.ActivityModule;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.AuthModule;
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.common.CommonModule;
import com.worldventures.dreamtrips.modules.facebook.FacebookModule;
import com.worldventures.dreamtrips.modules.feed.FeedModule;
import com.worldventures.dreamtrips.modules.friends.FriendsModule;
import com.worldventures.dreamtrips.modules.infopages.InfoModule;
import com.worldventures.dreamtrips.modules.membership.MembershipModule;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.reptools.ReptoolsModule;
import com.worldventures.dreamtrips.modules.trips.TripsModule;
import com.worldventures.dreamtrips.modules.tripsimages.TripsImagesModule;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public abstract class BaseActivity extends InjectingActivity {

    @Inject
    protected ActivityRouter router;

    @Inject
    protected FragmentCompass fragmentCompass;

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
        try {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 1) {
                fm.popBackStack();
            } else {
                finish();
            }
        } catch (IllegalStateException e) {
            Timber.w(e, "Problem on back pressed");
        }
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
        modules.add(new MembershipModule());
        modules.add(new FriendsModule());
        modules.add(new FeedModule());
        return modules;
    }

    public void onEvent(SessionHolder.Events.SessionDestroyed sessionDestroyed) {
        NavigationBuilder.create().with(router).move(Route.LOGIN);
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


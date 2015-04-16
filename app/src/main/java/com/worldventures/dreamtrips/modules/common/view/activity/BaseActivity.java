package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.activity.InjectingActivity;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.module.ActivityModule;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.AuthModule;
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.common.CommonModule;
import com.worldventures.dreamtrips.modules.facebook.FacebookModule;
import com.worldventures.dreamtrips.modules.infopages.InfoModule;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.reptools.ReptoolsModule;
import com.worldventures.dreamtrips.modules.trips.TripsModule;
import com.worldventures.dreamtrips.modules.tripsimages.TripsImagesModule;
import com.worldventures.dreamtrips.modules.video.VideoModule;

import net.hockeyapp.android.CrashManager;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public abstract class BaseActivity extends InjectingActivity {

    @Inject
    protected ActivityRouter router;

    @Inject
    protected UniversalImageLoader imageLoader;

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
        initHockeyApp();
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
            Timber.e(BaseActivity.class.getSimpleName(), e); //for avoid application crash when called at runtime
        }
    }

    protected List<Object> getModules() {
        List<Object> result = super.getModules();
        result.add(new ActivityModule(this));
        result.add(new AuthModule());
        result.add(new BucketListModule());
        result.add(new CommonModule());
        result.add(new FacebookModule());
        result.add(new InfoModule());
        result.add(new ProfileModule());
        result.add(new ReptoolsModule());
        result.add(new TripsModule());
        result.add(new TripsImagesModule());
        result.add(new VideoModule());
        return result;
    }

    public void onEvent(SessionHolder.Events.SessionDestroyed sessionDestroyed) {
        this.router.openLogin();
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


    protected void initHockeyApp() {
        CrashManager.register(this, BuildConfig.HOCKEY_APP_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}


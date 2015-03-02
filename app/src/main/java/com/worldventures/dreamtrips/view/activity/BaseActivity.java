package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.adobe.mobile.Config;
import com.instabug.library.util.TouchEventDispatcher;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.activity.InjectingActivity;
import com.worldventures.dreamtrips.core.api.spice.DreamSpiceManager;
import com.worldventures.dreamtrips.core.module.ActivityModule;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;

import net.hockeyapp.android.CrashManager;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public abstract class BaseActivity extends InjectingActivity {

    protected static final String HOCKEY_APP_ID = "4fc6063859b3388635cb834dbb004324";
    @Inject
    ActivityRouter router;
    @Inject
    UniversalImageLoader imageLoader;
    @Inject
    DreamSpiceManager spiceManager;

    private TouchEventDispatcher dispatcher = new TouchEventDispatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.setDebugLogging(true);
        Config.setContext(this.getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initHockeyApp();
        Config.collectLifecycleData(this);
    }


    @Override
    protected void onStart() {
        if (!spiceManager.isStarted()) spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (spiceManager.isStarted()) spiceManager.shouldStop();
        super.onStop();
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
        return result;
    }

    public void onEvent(SessionHolder.Events.SessionDestroyed sessionDestroyed) {
        this.router.finish();
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


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        dispatcher.dispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    protected void initHockeyApp() {
        CrashManager.register(this, HOCKEY_APP_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Config.pauseCollectingLifecycleData();
    }
}


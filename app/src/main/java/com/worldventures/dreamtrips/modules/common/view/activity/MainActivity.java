package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.AdapterView;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.ScreenOrientationChangeEvent;

import net.hockeyapp.android.UpdateManager;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.activity_main)
public class MainActivity extends PresentationModelDrivenActivity<MainActivityPresenter> implements MainActivityPresenter.View, NavigationDrawerListener {

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    @InjectView(R.id.container)
    View container;

    @Optional
    @InjectView(R.id.drawer)
    DrawerLayout drawerLayout;

    @Override
    protected MainActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new MainActivityPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresentationModel().create();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getPresentationModel().restoreInstanceState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeActionBarTransparent(false);
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (code != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(code, this, 0).show();
        }
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);

        setSupportActionBar(this.toolbar);

        if (!ViewUtils.isLandscapeOrientation(this)) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                    toolbar, R.string.drawer_open, R.string.drawer_close) {

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    disableRightDrawer();
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };

            // Set the drawer toggle as the DrawerListener
            drawerLayout.setDrawerListener(mDrawerToggle);
            this.drawerLayout.post(mDrawerToggle::syncState);
            //openLeftDrawer();
        }

        disableRightDrawer();
    }

    @Override
    public void onNavigationDrawerItemSelected(Route route) {
        closeLeftDrawer();
        makeActionBarTransparent(false);
        new Handler().postDelayed(() -> {
            getPresentationModel().selectItem(route);
            setTitle(route.getTitle());
        }, 150);
    }

    public void makeActionBarTransparent(boolean isTransparent) {
        if (isTransparent) {
            this.toolbar.getBackground().setAlpha(0);
            ((ViewGroup.MarginLayoutParams) container.getLayoutParams()).setMargins(0, 0, 0, 0);
        } else {
            this.toolbar.getBackground().setAlpha(255);
            int topMargin = ViewUtils.isLandscapeOrientation(this) ? 0 : getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
            ((ViewGroup.MarginLayoutParams) container.getLayoutParams()).setMargins(0, topMargin, 0, 0);
        }
    }

    public void openLeftDrawer() {
        if (!ViewUtils.isLandscapeOrientation(this))
            drawerLayout.openDrawer(Gravity.START);
    }

    public void closeLeftDrawer() {
        if (!ViewUtils.isLandscapeOrientation(this))
            drawerLayout.closeDrawer(Gravity.START);
    }

    public void openRightDrawer() {
        drawerLayout.openDrawer(Gravity.END);
        enableRightDrawer();
        //FiltersFragment filtersFragment = (FiltersFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_filters);
        //filtersFragment.refresh();
    }

    public void closeRightDrawer() {
        drawerLayout.closeDrawer(Gravity.END);
    }

    public void disableRightDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
    }

    public void enableRightDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupToolbarLayout();
        boolean landscapeOrientation = ViewUtils.isLandscapeOrientation(this);
        eventBus.post(new ScreenOrientationChangeEvent(landscapeOrientation));
    }

    private void setupToolbarLayout() {
        if (toolbar != null) {
            int size = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
            toolbar.setMinimumHeight(size);
            ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
            lp.height = size;
            toolbar.setLayoutParams(lp);
            // ((ViewGroup.MarginLayoutParams) container.getLayoutParams()).setMargins(0, ViewUtils.isLandscapeOrientation(this) ? 0 : size, 0, 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            closeRightDrawer();
        } else if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            closeLeftDrawer();
        } else {
            getPresentationModel().onBackPressed();
            super.onBackPressed();
        }
    }

    @Override
    protected void initHockeyApp() {
        super.initHockeyApp();
        if (BuildConfig.DEBUG) {
            UpdateManager.register(this, HOCKEY_APP_ID);
        }
    }

    @Override
    public void setTitle(int title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onDestroy() {
        unbindDrawables(findViewById(R.id.drawer));
        super.onDestroy();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null)
            view.getBackground().setCallback(null);

        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setImageBitmap(null);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                unbindDrawables(viewGroup.getChildAt(i));

            if (!(view instanceof AdapterView))
                viewGroup.removeAllViews();
        }
    }
}
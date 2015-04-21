package com.worldventures.dreamtrips.modules.common.view.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.WebViewReloadEvent;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationDrawerFragment;
import com.worldventures.dreamtrips.modules.infopages.InfoModule;
import com.worldventures.dreamtrips.modules.trips.TripsModule;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.activity_main)
public class MainActivity extends ActivityWithPresenter<MainActivityPresenter> implements MainActivityPresenter.View, NavigationDrawerListener {

    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    @InjectView(R.id.container)
    protected View container;

    @Optional
    @InjectView(R.id.container_bucket_details)
    protected FrameLayout detailsFrameLayout;

    @Optional
    @InjectView(R.id.container_edit)
    protected FrameLayout editFrameLayout;

    @Optional
    @InjectView(R.id.drawer)
    protected DrawerLayout drawerLayout;

    @InjectView(R.id.staticMenuLayout)
    protected FrameLayout staticMenuLayout;

    private NavigationDrawerFragment navigationDrawerFragment;
    private NavigationDrawerFragment navigationDrawerFragmentStatic;

    private int lastConfig;

    @Override
    protected MainActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new MainActivityPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_drawer);
        navigationDrawerFragmentStatic = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_drawer_static);

        checkGoogleServices();
    }

    private void checkGoogleServices() {
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (code != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(code, this, 0).show();
        }
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
        disableRightDrawer();
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
        }

    }

    @Override
    public void onNavigationDrawerItemSelected(ComponentDescription route) {
        closeLeftDrawer();
        makeActionBarTransparent(false);

        if (route != null) {
            openComponent(route, true);

            if (detailsFrameLayout != null) {
                detailsFrameLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNavigationDrawerItemReselected(ComponentDescription route) {
        eventBus.post(new WebViewReloadEvent());
        if (route != null) {
            if (route.getKey().equals(TripsModule.OTA) ||
                    route.getKey().equals(InfoModule.FAQ) ||
                    route.getKey().equals(InfoModule.TERMS_OF_SERVICE)) {
                openComponent(route, false);
            }
        }
        closeLeftDrawer();
    }

    private void openComponent(ComponentDescription route, boolean backstack) {
        final String className = route.getFragmentClass().getName();
        BaseFragment fragment = (BaseFragment) Fragment.instantiate(this, className);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);

        if (backstack) {
            fragmentTransaction.addToBackStack(route.getKey());
        }

        fragmentTransaction.commit();

        setTitle(route.getTitle());
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
        if (!ViewUtils.isLandscapeOrientation(this)) {
            drawerLayout.openDrawer(Gravity.START);
        }
    }

    public void closeLeftDrawer() {
        if (!ViewUtils.isLandscapeOrientation(this)) {
            drawerLayout.closeDrawer(Gravity.START);
        }
    }

    public void openRightDrawer() {
        drawerLayout.openDrawer(Gravity.END);
        enableRightDrawer();
    }

    public void closeRightDrawer() {
        drawerLayout.closeDrawer(Gravity.END);
    }

    public void disableLeftDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.START);
    }

    public void enableLeftDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.START);
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

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            enableLeftDrawer();
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            staticMenuLayout.setVisibility(View.GONE);
        } else {
            disableLeftDrawer();
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            staticMenuLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setupToolbarLayout() {
        if (toolbar != null) {
            int size = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
            toolbar.setMinimumHeight(size);
            ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
            lp.height = size;
            toolbar.setLayoutParams(lp);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            closeRightDrawer();
        } else if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            closeLeftDrawer();
        } else if (editFrameLayout != null &&
                editFrameLayout.getVisibility() == View.VISIBLE) {
            editFrameLayout.setVisibility(View.GONE);
        } else if (detailsFrameLayout != null &&
                detailsFrameLayout.getVisibility() == View.VISIBLE) {
            detailsFrameLayout.setVisibility(View.GONE);
        } else {
            if (navigationDrawerFragment != null) {
                navigationDrawerFragment.onBackPressed();
                navigationDrawerFragmentStatic.onBackPressed();
            }

            if (detailsFrameLayout != null) {
                detailsFrameLayout.setVisibility(View.GONE);
            }

            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(int title) {
        getSupportActionBar().setTitle(title);
    }
}

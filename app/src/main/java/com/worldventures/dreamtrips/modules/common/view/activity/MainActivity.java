package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.MenuPressedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationDrawerFragment;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;
import icepick.State;
import com.messenger.ui.activity.MessengerStartActivity;


@Layout(R.layout.activity_main)
public class MainActivity extends ActivityWithPresenter<MainActivityPresenter>
        implements MainActivityPresenter.View, NavigationDrawerListener {

    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;
    @InjectView(R.id.container_wrapper)
    protected View wrapperContainer;
    @InjectView(R.id.container_main)
    protected View mainContainer;
    @Optional
    @InjectView(R.id.container_details_floating)
    protected FrameLayout detailsFloatingContainer;
    @InjectView(R.id.drawer)
    protected DrawerLayout drawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    @Inject
    protected RootComponentsProvider rootComponentsProvider;

    @State
    protected ComponentDescription currentComponent;
    @State
    protected boolean toolbarGone;

    private NavigationDrawerFragment navigationDrawerFragment;

    @Override
    protected MainActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new MainActivityPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeActionBarGone(toolbarGone);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        TrackingHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        TrackingHelper.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        setSupportActionBar(this.toolbar);
        setUpBurger();
        setUpMenu();
        //
        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_drawer);

        BaseFragment currentFragment = fragmentCompass.getCurrentFragment();
        if (currentComponent == null && currentFragment != null) {
            currentComponent = rootComponentsProvider.getComponentByFragment(currentFragment.getClass());
        }
        if (currentComponent == null) {
            currentComponent = rootComponentsProvider.getActiveComponents().get(0);
        }
        if (currentFragment == null) {
            onNavigationDrawerItemSelected(currentComponent);
        } else {
            setTitle(currentComponent.getToolbarTitle());
            navigationDrawerFragment.setCurrentComponent(currentComponent);
        }
    }

    @Override
    public void setTitle(int title) {
        if (title != 0)
            getSupportActionBar().setTitle(title);
        else
            getSupportActionBar().setTitle("");
    }

    @Override
    public void makeActionBarGone(boolean hide) {
        this.toolbarGone = hide;
        if (hide) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
            toolbar.getBackground().setAlpha(255);
        }
    }

    private void setUpBurger() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                disableRightDrawer();
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                SoftInputUtil.hideSoftInputMethod(MainActivity.this);
                eventBus.post(new MenuPressedEvent());
            }
        };

        drawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        this.drawerLayout.post(mDrawerToggle::syncState);
    }

    private void setUpMenu() {
        disableRightDrawer();
        if (!ViewUtils.isLandscapeOrientation(this)) {
            enableLeftDrawer();
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        } else {
            disableLeftDrawer();
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(ComponentDescription component) {
        //navigate to messenger
        if (component.getKey().equals("Messenger")) {
            startActivity(new Intent(this, MessengerStartActivity.class));
            return;
        }
        //
        eventBus.post(new MenuPressedEvent());
        //
        closeLeftDrawer();
        disableRightDrawer();
        makeActionBarGone(component.isSkipGeneralToolbar());
        //
        navigationDrawerFragment.setCurrentComponent(component);
        currentComponent = component;
        getPresentationModel().openComponent(component);
    }

    @Override
    public void onNavigationDrawerItemReselected(ComponentDescription route) {
        closeLeftDrawer();
    }

    boolean handleBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            closeRightDrawer();
            return true;
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeLeftDrawer();
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Drawer helpers
    ///////////////////////////////////////////////////////////////////////////

    public void openLeftDrawer() {
        if (!ViewUtils.isLandscapeOrientation(this)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public void closeLeftDrawer() {
        if (!ViewUtils.isLandscapeOrientation(this)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void openRightDrawer() {
        drawerLayout.openDrawer(GravityCompat.END);
        enableRightDrawer();
    }

    public void closeRightDrawer() {
        drawerLayout.closeDrawer(GravityCompat.END);
    }

    public void disableLeftDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
    }

    public void enableLeftDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
    }

    public void disableRightDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
    }

    public void enableRightDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
    }

    @Override
    public void onBackPressed() {
        if (!handleBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onTopLevelBackStackPopped() {
        super.onTopLevelBackStackPopped();
        updateTitle();
    }

    protected void updateTitle() {
        currentComponent = this.rootComponentsProvider.getComponent(getSupportFragmentManager());
        //
        if (rootComponentsProvider.getActiveComponents().contains(currentComponent)) {
            navigationDrawerFragment.setCurrentComponent(currentComponent);
            setTitle(currentComponent.getToolbarTitle());
            makeActionBarGone(currentComponent.isSkipGeneralToolbar());
        }
    }

}

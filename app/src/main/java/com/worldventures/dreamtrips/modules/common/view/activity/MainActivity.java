package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.worldventures.dreamtrips.modules.common.event.BackPressedMessageEvent;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationDrawerFragment;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;
import icepick.State;


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
    protected void afterCreateView(Bundle savedInstanceState) {
        setupToolbar();
        super.afterCreateView(savedInstanceState);
        fragmentCompass.clear();
        //
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

    private void setupToolbar() {
        setSupportActionBar(this.toolbar);
    }

    @Override
    public void setTitle(int title) {
        if (title != 0)
            getSupportActionBar().setTitle(title);
        else
            getSupportActionBar().setTitle("");
    }

    @Override
    public void makeActionBarGone(boolean gone) {
        this.toolbarGone = gone;
        if (gone) {
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
        eventBus.post(new MenuPressedEvent());

        closeLeftDrawer();
        disableRightDrawer();
        makeActionBarGone(false);

        navigationDrawerFragment.setCurrentComponent(component);
        currentComponent = component;
        getPresentationModel().openComponent(component);

        handleComponentChange();
    }

    @Override
    public void updateSelection(ComponentDescription component) {
        currentComponent = component;
        navigationDrawerFragment.setCurrentComponent(component);
    }

    @Override
    public void onNavigationDrawerItemReselected(ComponentDescription route) {
        closeLeftDrawer();
    }

    boolean handleComponentChange() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            closeRightDrawer();
            return true;
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeLeftDrawer();
            return true;
        } else if (detailsFloatingContainer != null && detailsFloatingContainer.getVisibility() == View.VISIBLE) {
            fragmentCompass.removePost();
            fragmentCompass.removeEdit();
            detailsFloatingContainer.setVisibility(View.GONE);
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

    public void onEvent(BackPressedMessageEvent event) {
        if (isVisibleOnScreen() && !handleComponentChange()) {
            fragmentCompass.clear();
            FragmentManager fm = getSupportFragmentManager();
            int entryCount = fm.getBackStackEntryCount();
            if (entryCount >= 2) {
                int backOffset = 1;
                do {
                    currentComponent = this.rootComponentsProvider.getComponent(fm, backOffset);
                    backOffset++;
                }
                while (!rootComponentsProvider.getActiveComponents().contains(currentComponent) &&
                        backOffset <= entryCount);
                navigationDrawerFragment.setCurrentComponent(currentComponent);
                setTitle(currentComponent.getToolbarTitle());
            }

            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        eventBus.post(new BackPressedMessageEvent());
    }

}

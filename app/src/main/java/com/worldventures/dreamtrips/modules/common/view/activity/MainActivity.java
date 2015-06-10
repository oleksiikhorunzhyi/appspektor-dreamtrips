package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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
import com.worldventures.dreamtrips.core.utils.events.WebViewReloadEvent;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationDrawerFragment;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;
import icepick.Icepick;
import icepick.Icicle;

@Layout(R.layout.activity_main)
public class MainActivity extends ActivityWithPresenter<MainActivityPresenter>
        implements MainActivityPresenter.View, NavigationDrawerListener {

    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;
    @InjectView(R.id.container_wrapper)
    protected View wrapperContainer;
    @InjectView(R.id.container_main)
    protected View mainContainer;
    @InjectView(R.id.container_details)
    protected FrameLayout detailsContainer;
    @Optional
    @InjectView(R.id.container_details_floating)
    protected FrameLayout detailsFloatingContainer;
    @InjectView(R.id.container_details_fullscreen)
    protected FrameLayout detailsFullScreenContainer;
    @InjectView(R.id.drawer)
    protected DrawerLayout drawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    @Inject
    protected RootComponentsProvider rootComponentsProvider;

    @Icicle
    protected ComponentDescription currentComponent;
    @Icicle
    protected boolean transparentToolbar;

    private NavigationDrawerFragment navigationDrawerFragment;

    @Override
    protected MainActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new MainActivityPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresentationModel().showUserIfNeeded();
        makeActionBarTransparent(transparentToolbar);
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

    public void makeActionBarTransparent(boolean isTransparent) {
        if (ViewUtils.isLandscapeOrientation(this)) isTransparent = false;
        //
        this.transparentToolbar = isTransparent;
        int contentPadding = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
        if (isTransparent) {
            toolbar.getBackground().setAlpha(0);
            wrapperContainer.setPadding(0, 0, 0, 0);
        } else {
            toolbar.getBackground().setAlpha(255);
            wrapperContainer.setPadding(0, contentPadding, 0, 0);
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

        handleComponentChange();
        disableRightDrawer();
        makeActionBarTransparent(false);

        navigationDrawerFragment.setCurrentComponent(component);
        currentComponent = component;
        getPresentationModel().openComponent(component);
    }

    @Override
    public void updateSelection(ComponentDescription component) {
        currentComponent = component;
        navigationDrawerFragment.setCurrentComponent(component);
    }

    @Override
    public void onNavigationDrawerItemReselected(ComponentDescription route) {
        eventBus.post(new WebViewReloadEvent());
        closeLeftDrawer();
    }

    boolean handleComponentChange() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            closeRightDrawer();
            return true;
        } else if (drawerLayout.isDrawerOpen(Gravity.START)) {
            closeLeftDrawer();
            return true;
        } else if (detailsFullScreenContainer.getVisibility() == View.VISIBLE) {
            fragmentCompass.removeEdit();
            detailsFullScreenContainer.setVisibility(View.GONE);
            return true;
        } else if (detailsContainer.getVisibility() == View.VISIBLE) {
            fragmentCompass.removeDetailed();
            detailsContainer.setVisibility(View.GONE);
            return true;
        } else if (detailsFloatingContainer != null && detailsFloatingContainer.getVisibility() == View.VISIBLE) {
            fragmentCompass.removeDetailed();
            detailsFloatingContainer.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!handleComponentChange()) {
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

    ///////////////////////////////////////////////////////////////////////////
    // Drawer helpers
    ///////////////////////////////////////////////////////////////////////////

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

}

package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.MenuPressedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerViewImpl;

import javax.inject.Inject;

import butterknife.InjectView;
import icepick.State;


@Layout(R.layout.activity_main)
public class MainActivity extends ActivityWithPresenter<MainActivityPresenter>
        implements MainActivityPresenter.View {

    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;
    @InjectView(R.id.drawer)
    protected DrawerLayout drawerLayout;
    @InjectView(R.id.drawer_layout)
    protected NavigationDrawerViewImpl navDrawer;

    private ActionBarDrawerToggle mDrawerToggle;

    @Inject
    protected RootComponentsProvider rootComponentsProvider;

    @State
    protected ComponentDescription currentComponent;
    @State
    protected boolean toolbarGone;

    protected NavigationDrawerPresenter navigationDrawerPresenter;

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
        Fragment currentFragment = fragmentCompass.getCurrentFragment();
        if (currentComponent == null && currentFragment != null) {
            currentComponent = rootComponentsProvider.getComponentByFragment(currentFragment.getClass());
        }
        if (currentComponent == null) {
            currentComponent = rootComponentsProvider.getActiveComponents().get(0);
        }
        //
        initNavDrawer();
        //
        if (currentFragment == null) {
            itemSelected(currentComponent);
        } else {
            setTitle(currentComponent.getToolbarTitle());
            navigationDrawerPresenter.setCurrentComponent(currentComponent);
        }
    }

    private void initNavDrawer() {
        navigationDrawerPresenter = new NavigationDrawerPresenter();
        inject(navigationDrawerPresenter);
        //
        navigationDrawerPresenter.attachView(navDrawer, rootComponentsProvider.getActiveComponents());
        navigationDrawerPresenter.setOnItemReselected(this::itemReseleted);
        navigationDrawerPresenter.setOnItemSelected(this::itemSelected);
        navigationDrawerPresenter.setOnLogout(this::logout);
    }

    @Override
    public void onDestroy() {
        navigationDrawerPresenter.detach();
        super.onDestroy();
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

    private void itemSelected(ComponentDescription component) {
        //navigate to messenger
        /*if (component.getKey().equals("Messenger")) {
            MessengerStartActivity.start(this);
            return;
        }
        */
        currentComponent = component;
        //
        eventBus.post(new MenuPressedEvent());
        //
        closeLeftDrawer();
        disableRightDrawer();
        makeActionBarGone(component.isSkipGeneralToolbar());
        //
        navigationDrawerPresenter.setCurrentComponent(component);
        getPresentationModel().openComponent(component);
    }

    private void itemReseleted(ComponentDescription route) {
        closeLeftDrawer();
    }

    private void logout() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.logout_dialog_title))
                .content(getString(R.string.logout_dialog_message))
                .positiveText(getString(R.string.logout_dialog_positive_btn))
                .negativeText(getString(R.string.logout_dialog_negative_btn))
                .positiveColorRes(R.color.theme_main_darker)
                .negativeColorRes(R.color.theme_main_darker)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        TrackingHelper.logout();
                        getPresentationModel().logout();
                    }
                }).show();

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
            navigationDrawerPresenter.setCurrentComponent(currentComponent);
            setTitle(currentComponent.getToolbarTitle());
            makeActionBarGone(currentComponent.isSkipGeneralToolbar());
        }
    }

}

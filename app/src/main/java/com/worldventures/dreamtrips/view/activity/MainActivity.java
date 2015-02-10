package com.worldventures.dreamtrips.view.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.presentation.MainActivityPresentation;
import com.worldventures.dreamtrips.utils.ViewUtils;
import com.worldventures.dreamtrips.utils.busevents.ScreenOrientationChangeEvent;
import com.worldventures.dreamtrips.view.fragment.FiltersFragment;

import net.hockeyapp.android.UpdateManager;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.activity_main)
public class MainActivity extends PresentationModelDrivenActivity<MainActivityPresentation> implements MainActivityPresentation.View, NavigationDrawerListener {

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    @InjectView(R.id.container)
    View container;

    @Optional
    @InjectView(R.id.drawer)
    DrawerLayout drawerLayout;

    @Override
    protected MainActivityPresentation createPresentationModel(Bundle savedInstanceState) {
        return new MainActivityPresentation(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresentationModel().create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeActionBarTransparent(false);
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
    public void onNavigationDrawerItemSelected(State state) {
        closeLeftDrawer();
        getPresentationModel().selectItem(state);
        getSupportActionBar().setTitle(state.getTitle());
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
        FiltersFragment filtersFragment = (FiltersFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_filters);
        filtersFragment.refresh();
    }

    public void closeRightDrawer() {
        drawerLayout.closeDrawer(Gravity.END);
    }

    public void disableRightDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
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
        getPresentationModel().onBackPressed();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void initHockeyApp() {
        super.initHockeyApp();
        if (BuildConfig.DEBUG) {
            UpdateManager.register(this, HOCKEY_APP_ID);
        }
    }

    @Override
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}

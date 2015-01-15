package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerCallbacks;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerFragment;
import com.worldventures.dreamtrips.presentation.MainActivityPresentation;

import butterknife.InjectView;

@Layout(R.layout.activity_main)
public class MainActivity extends PresentationModelDrivenActivity<MainActivityPresentation> implements MainActivityPresentation.View, NavigationDrawerCallbacks {

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.container)
    View container;

    private NavigationDrawerFragment navigationDrawerFragment;

    @Override
    protected MainActivityPresentation createPresentationModel(Bundle savedInstanceState) {
        return new MainActivityPresentation(this);
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        navigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        getPresentationModel().onNavigationClick(position);
    }

    @Override
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void resetActionBar() {
        makeActionBarTransparent(false);
    }

    public void makeActionBarTransparent(boolean isTransparent) {
        if (isTransparent) {
            toolbar.getBackground().setAlpha(0);
            ((ViewGroup.MarginLayoutParams) container.getLayoutParams()).setMargins(0, 0, 0, 0);
        } else {
            toolbar.getBackground().setAlpha(255);
            int topMargin = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
            ((ViewGroup.MarginLayoutParams) container.getLayoutParams()).setMargins(0, topMargin, 0, 0);
        }
    }
}

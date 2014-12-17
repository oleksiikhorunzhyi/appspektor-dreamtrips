package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerCallbacks;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerFragment;
import com.worldventures.dreamtrips.view.presentation.MainActivityPresentation;

public class MainActivity extends BaseActivity implements MainActivityPresentation.View, NavigationDrawerCallbacks {

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void tripsLoaded() {
        //todo now nothing
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }
}

package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;
import com.worldventures.dreamtrips.presentation.MainActivityPresentation;
import com.worldventures.dreamtrips.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.view.fragment.DreamTripsFragment;
import com.worldventures.dreamtrips.view.fragment.ProfileFragment;
import com.worldventures.dreamtrips.view.fragment.StaticInfoFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesTabsFragment;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.activity_main)
public class MainActivity extends PresentationModelDrivenActivity<MainActivityPresentation> implements MainActivityPresentation.View, NavigationDrawerListener {

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    @InjectView(R.id.container)
    View container;

    private static class MenuElement {
        final Class<? extends Fragment> fragmentClass;
        final String title;
        final int iconResource;

        private MenuElement(Class<? extends Fragment> fragmentClass, String title, int iconResource) {
            this.fragmentClass = fragmentClass;
            this.title = title;
            this.iconResource = iconResource;
        }
    }

    List<MenuElement> drawerElements = new ArrayList<>();

    @Override
    protected MainActivityPresentation createPresentationModel(Bundle savedInstanceState) {
        return new MainActivityPresentation(this);
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);

        this.drawerElements.add(new MenuElement(DreamTripsFragment.class, "DreamTrips", R.drawable.ic_dreamtrips));
        this.drawerElements.add(new MenuElement(TripImagesTabsFragment.class, "Trip Images", R.drawable.ic_trip_images));
        this.drawerElements.add(new MenuElement(DreamTripsFragment.class, "Membership", R.drawable.ic_membership));
        this.drawerElements.add(new MenuElement(DreamTripsFragment.class, "Bucket list", R.drawable.ic_bucket_lists));
        this.drawerElements.add(new MenuElement(ProfileFragment.class, "My profile", R.drawable.ic_profile));
        this.drawerElements.add(new MenuElement(StaticInfoFragment.FAQFragment.class, "FAQ", R.drawable.ic_faq));
        this.drawerElements.add(new MenuElement(StaticInfoFragment.TermsAndConditionsFragment.class, "Terms&Conditions", R.drawable.ic_termsconditions));

        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        navigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), this.toolbar);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        MenuElement menuElement = drawerElements.get(position);

        final String className = menuElement.fragmentClass.getName();

        BaseFragment fragment = (BaseFragment) Fragment.instantiate(this, className);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(className);

        if (BuildConfig.DEBUG) {
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.commitAllowingStateLoss();
        }

        getSupportActionBar().setTitle(menuElement.title);
    }

    public void makeActionBarTransparent(boolean isTransparent) {
        if (isTransparent) {
            this.toolbar.getBackground().setAlpha(0);
            ((ViewGroup.MarginLayoutParams) container.getLayoutParams()).setMargins(0, 0, 0, 0);
        } else {
            this.toolbar.getBackground().setAlpha(255);
            int topMargin = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
            ((ViewGroup.MarginLayoutParams) container.getLayoutParams()).setMargins(0, topMargin, 0, 0);
        }
    }
}

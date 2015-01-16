package com.worldventures.dreamtrips.view.fragment.navigationdrawer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.core.session.AppSessionHolder;
import com.worldventures.dreamtrips.presentation.BasePresentation;
import com.worldventures.dreamtrips.presentation.NavigationDrawerPM;
import com.worldventures.dreamtrips.utils.busevents.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.view.fragment.BaseFragment;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

@Layout(R.layout.fragment_navigation_drawer)
public class NavigationDrawerFragment extends BaseFragment<NavigationDrawerPM> implements BasePresentation.View, NavigationDrawerListener {

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREFERENCES_FILE = "my_app_settings"; //TODO: change this to your file

    @Inject
    @Global
    EventBus eventBus;

    @Inject
    AppSessionHolder appSessionHolder;

    private NavigationDrawerListener navigationDrawerListener;

    @InjectView(R.id.drawerList)
    RecyclerView drawerList;

    private View fragmentContainerView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private boolean userLearnedDrawer;
    private boolean fromSavedInstanceState;
    private int currentSelectedPosition;
    private NavigationDrawerAdapter adapter;

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    private NavigationHeader getNavigationHeader() {
        NavigationHeader navHeader = new NavigationHeader();

        User user = appSessionHolder.get().get().getUser();

        navHeader.setUserEmail(user.getEmail());
        navHeader.setUserNome(user.getUsername());
        navHeader.setUserCover(Uri.fromFile(new File(user.getCoverPath())));
        navHeader.setUserPhoto(user.getAvatar().getMediumUri());

        return navHeader;
    }

    public void onEvent(UpdateUserInfoEvent event) {
        updateHeader();
    }

    private void updateHeader() {
        adapter.setHeader(getNavigationHeader());
        adapter.notifyItemChanged(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLearnedDrawer = Boolean.valueOf(readSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            fromSavedInstanceState = true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            navigationDrawerListener = (NavigationDrawerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    protected NavigationDrawerPM createPresentationModel(Bundle savedInstanceState) {
        return new NavigationDrawerPM(this);
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {

        drawerList = (RecyclerView) getActivity().findViewById(R.id.drawerList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        drawerList.setLayoutManager(layoutManager);
        drawerList.setHasFixedSize(true);

        adapter = new NavigationDrawerAdapter(getMenu(), (Injector) getActivity());
        adapter.setNavigationDrawerCallbacks(this);

        updateHeader();

        drawerList.setAdapter(adapter);

        fragmentContainerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;
        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), NavigationDrawerFragment.this.drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (isAdded()) {
                    getActivity().invalidateOptionsMenu();
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (isAdded()) {
                    if (!userLearnedDrawer) {
                        userLearnedDrawer = true;
                        saveSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "true");
                    }

                    getActivity().invalidateOptionsMenu();
                }
            }
        };

        if (!userLearnedDrawer && !fromSavedInstanceState) {
            this.drawerLayout.openDrawer(fragmentContainerView);
        }

        this.drawerLayout.post(actionBarDrawerToggle::syncState);

        this.drawerLayout.setDrawerListener(actionBarDrawerToggle);
        selectItem(currentSelectedPosition);
        eventBus.register(this);
    }

    public void openDrawer() {
        this.drawerLayout.openDrawer(this.fragmentContainerView);
    }

    public void closeDrawer() {
        this.drawerLayout.closeDrawer(this.fragmentContainerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.navigationDrawerListener = null;
    }

    public List<State> getMenu() {
        return State.getMenuItemsArray();
    }

    void selectItem(int position) {
        this.currentSelectedPosition = position;
        if (this.drawerLayout != null) {
            this.drawerLayout.closeDrawer(this.fragmentContainerView);
        }
        if (this.navigationDrawerListener != null) {
            this.navigationDrawerListener.onNavigationDrawerItemSelected(position);
        }
        ((NavigationDrawerAdapter) this.drawerList.getAdapter()).selectPosition(position);
    }

    public boolean isDrawerOpen() {
        return this.drawerLayout != null && this.drawerLayout.isDrawerOpen(this.fragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        selectItem(position);
    }
}
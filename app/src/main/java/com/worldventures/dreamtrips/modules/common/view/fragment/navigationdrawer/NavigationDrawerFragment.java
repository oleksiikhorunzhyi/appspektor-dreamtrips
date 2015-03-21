package com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.UpdateSelectionEvent;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.modules.auth.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;
import com.worldventures.dreamtrips.modules.common.presenter.NavigationDrawerPM;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

@Layout(R.layout.fragment_navigation_drawer)
public class NavigationDrawerFragment extends BaseFragment<NavigationDrawerPM> implements BasePresenter.View, NavigationDrawerListener {

    private static final String STATE_SELECTED_STATE = "selected_navigation_drawer_state";

    @Inject
    @Global
    EventBus eventBus;

    @Inject
    SessionHolder<UserSession> appSessionHolder;

    @Inject
    FragmentCompass fragmentCompass;
    @InjectView(R.id.drawerList)
    RecyclerView drawerList;

    private NavigationDrawerListener navigationDrawerListener;
    private Route savedRoute = Route.DREAMTRIPS;
    private NavigationDrawerAdapter adapter;

    private boolean instanceSaved = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navigationDrawerListener = (NavigationDrawerListener) getActivity();
        if (savedInstanceState != null) {
            savedRoute = (Route) savedInstanceState.getSerializable(STATE_SELECTED_STATE);
            instanceSaved = true;
        }
        adapter.selectPosition(ViewUtils.isLandscapeOrientation(getActivity()) ?
                savedRoute.getPosition() : savedRoute.getPosition() + 1);
        selectItem(savedRoute);
    }

    @Override
    protected NavigationDrawerPM createPresenter(Bundle savedInstanceState) {
        return new NavigationDrawerPM(this);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        drawerList.setLayoutManager(layoutManager);

        adapter = new NavigationDrawerAdapter(getMenu(), (Injector) getActivity());
        adapter.setNavigationDrawerCallbacks(this);

        if (!ViewUtils.isLandscapeOrientation(getActivity())) {
            adapter.setHeader(getNavigationHeader());
        }

        drawerList.setAdapter(adapter);
        eventBus.register(this);
    }

    private NavigationHeader getNavigationHeader() {
        NavigationHeader navHeader = new NavigationHeader();

        User user = appSessionHolder.get().get().getUser();

        navHeader.setUserEmail(user.getEmail());
        navHeader.setUserNome(user.getUsername());
        navHeader.setUserCover(Uri.fromFile(new File(user.getCoverPath())));
        navHeader.setUserPhoto(Uri.parse(user.getAvatar().getMedium()));

        return navHeader;
    }

    public void onEvent(UpdateSelectionEvent event) {
        Route route = fragmentCompass.getPreviousFragment();
        instanceSaved = true;
        savedRoute = route;
        adapter.selectPosition(ViewUtils.isLandscapeOrientation(getActivity()) ?
                route.getPosition() : route.getPosition() + 1);
    }

    public void onEvent(UpdateUserInfoEvent event) {
        updateHeader();
    }

    private void updateHeader() {
        if (!ViewUtils.isLandscapeOrientation(getActivity())) {
            adapter.setHeader(getNavigationHeader());
            adapter.notifyItemChanged(0);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.navigationDrawerListener = null;
    }

    public List<Route> getMenu() {
        return Route.getMenuItemsArray();
    }

    void selectItem(Route route) {
        if ((!instanceSaved || !route.equals(savedRoute)) && this.navigationDrawerListener != null) {
            this.navigationDrawerListener.onNavigationDrawerItemSelected(route);
            instanceSaved = false;
        }
        savedRoute = route;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_SELECTED_STATE, savedRoute);
    }

    @Override
    public void onNavigationDrawerItemSelected(Route route) {
        selectItem(route);
    }
}
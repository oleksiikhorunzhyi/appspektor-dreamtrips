package com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.fragment_navigation_drawer)
public class NavigationDrawerFragment extends BaseFragment<Presenter> implements Presenter.View, NavigationDrawerListener {

    private static final String STATE_SELECTED_STATE = "selected_navigation_drawer_state";

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

    @InjectView(R.id.drawerList)
    protected RecyclerView drawerList;

    @Inject
    protected RootComponentsProvider rootComponentsProvider;

    private NavigationDrawerListener navigationDrawerListener;
    private NavigationDrawerAdapter adapter;
    private ComponentDescription currentComponent;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.navigationDrawerListener = (NavigationDrawerListener) getActivity();

        ComponentDescription componentDescription;
        if (savedInstanceState != null) {
            componentDescription = this.rootComponentsProvider.getActiveComponents().get(savedInstanceState.getInt(STATE_SELECTED_STATE));
            setCurrentComponent(componentDescription);
        } else {
            componentDescription = this.rootComponentsProvider.getActiveComponents().get(0);
            setCurrentComponent(componentDescription);
        }
    }

    @Override
    protected Presenter createPresenter(Bundle savedInstanceState) {
        return new Presenter(this);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        drawerList.setLayoutManager(layoutManager);

        setAdapter();
    }

    private void setAdapter() {
        adapter = new NavigationDrawerAdapter(new ArrayList<>(this.rootComponentsProvider.getActiveComponents()), (Injector) getActivity());
        adapter.setNavigationDrawerCallbacks(this);

        if (!ViewUtils.isLandscapeOrientation(getActivity())) {
            adapter.setHeader(getNavigationHeader());
        }

        drawerList.setAdapter(adapter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setAdapter();
    }

    private NavigationHeader getNavigationHeader() {
        return new NavigationHeader(appSessionHolder.get().get().getUser());
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

    void selectItem(ComponentDescription componentDescription) {
        if (this.navigationDrawerListener != null) {
            final boolean shouldUpdateComponentSelection = currentComponent == null
                    || !componentDescription.getKey().equalsIgnoreCase(currentComponent.getKey());

            if (shouldUpdateComponentSelection) {
                this.navigationDrawerListener.onNavigationDrawerItemSelected(componentDescription);
            } else {
                this.navigationDrawerListener.onNavigationDrawerItemReselected(componentDescription);
            }
        }

        setCurrentComponent(componentDescription);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_STATE, this.rootComponentsProvider.getActiveComponents().indexOf(currentComponent));
    }

    @Override
    public void onNavigationDrawerItemSelected(ComponentDescription componentDescription) {
        selectItem(componentDescription);
    }

    @Override
    public void onNavigationDrawerItemReselected(ComponentDescription componentDescription) {
        //nothing to do here
    }

    public void onBackPressed() {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        if (fm.getBackStackEntryCount() >= 2) {
            final int index = fm.getBackStackEntryCount() - 2;
            final FragmentManager.BackStackEntry backEntry = fm.getBackStackEntryAt(index);
            setCurrentComponent(this.rootComponentsProvider.getComponentByKey(backEntry.getName()));
        }
    }

    public void updateSelection() {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        final int index = fm.getBackStackEntryCount() - 1;
        final FragmentManager.BackStackEntry backEntry = fm.getBackStackEntryAt(index);
        setCurrentComponent(this.rootComponentsProvider.getComponentByKey(backEntry.getName()));
    }

    public void setCurrentComponent(ComponentDescription currentComponent) {
        this.currentComponent = currentComponent;

        if (this.currentComponent != null) {
            final int componentIndex = this.rootComponentsProvider.getActiveComponents().indexOf(currentComponent);
            adapter.selectPosition(ViewUtils.isLandscapeOrientation(getActivity()) ?
                    componentIndex : componentIndex + 1);

            if (getActivity() != null) {
                getActivity().setTitle(currentComponent.getTitle());
            }
        }
    }
}
package com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    private NavigationDrawerListener targetDrawerListener;
    private NavigationDrawerAdapter adapter;
    private ComponentDescription currentComponent;

    @Override
    protected Presenter createPresenter(Bundle savedInstanceState) {
        return new Presenter(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.targetDrawerListener = (NavigationDrawerListener) activity;
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        adapter = new NavigationDrawerAdapter(new ArrayList<>(this.rootComponentsProvider.getActiveComponents()), (Injector) getActivity());
        adapter.setNavigationDrawerCallbacks(this);
        adapter.setHasStableIds(true);
        drawerList.setAdapter(adapter);
        drawerList.setLayoutManager(
                new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false)
        );
        updateHeader();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateHeader();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.targetDrawerListener = null;
    }

    private NavigationHeader createNavigationHeader() {
        return new NavigationHeader(appSessionHolder.get().get().getUser());
    }

    private void updateHeader() {
        if (ViewUtils.isLandscapeOrientation(getActivity())) {
            if (adapter.setHeader(null)) {
                adapter.notifyItemRemoved(0);
            }
        } else {
            if (adapter.setHeader(createNavigationHeader())) {
                adapter.notifyItemInserted(0);
            } else {
                adapter.notifyItemChanged(0);
            }
        }
    }

    public void onEvent(UpdateUserInfoEvent event) {
        updateHeader();
    }

    public void hide() {
        getView().setVisibility(View.GONE);
    }

    public void show() {
        getView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onNavigationDrawerItemSelected(ComponentDescription newComponent) {
        if (this.targetDrawerListener != null) {
            boolean updateComponentSelection = !newComponent.getKey().equalsIgnoreCase(currentComponent.getKey());

            if (updateComponentSelection) {
                this.targetDrawerListener.onNavigationDrawerItemSelected(newComponent);
            } else {
                this.targetDrawerListener.onNavigationDrawerItemReselected(newComponent);
            }
        }
    }

    @Override
    public void onNavigationDrawerItemReselected(ComponentDescription componentDescription) {
        //nothing to do here
    }

    public void setCurrentComponent(ComponentDescription currentComponent) {
        this.currentComponent = currentComponent;
        adapter.selectComponent(currentComponent);
    }
}
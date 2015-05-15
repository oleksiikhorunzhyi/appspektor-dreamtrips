package com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
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
        initUI();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initUI();
        setCurrentComponent(currentComponent);
    }

    private void initUI() {
        drawerList.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
        );
        adapter = new NavigationDrawerAdapter(new ArrayList<>(this.rootComponentsProvider.getActiveComponents()), (Injector) getActivity());
        adapter.setNavigationDrawerCallbacks(this);
        drawerList.setAdapter(adapter);
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
            boolean updateComponentSelection = currentComponent != null ?
                    !newComponent.getKey().equalsIgnoreCase(currentComponent.getKey()) :
                    true;

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

    public void setCurrentComponent(ComponentDescription newComponent) {
        drawerList.post(() -> {
            currentComponent = newComponent;
            adapter.selectComponent(newComponent);
        });
    }
}
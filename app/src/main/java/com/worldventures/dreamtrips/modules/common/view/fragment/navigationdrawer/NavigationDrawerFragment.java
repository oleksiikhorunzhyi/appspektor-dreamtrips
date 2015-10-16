package com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.modules.common.presenter.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.fragment_navigation_drawer)
public class NavigationDrawerFragment extends BaseFragment<NavigationDrawerPresenter> implements NavigationDrawerPresenter.View, NavigationDrawerListener {

    @Inject
    @ForApplication
    Injector injector;
    @Inject
    protected SessionHolder<UserSession> appSessionHolder;
    @Inject
    protected RootComponentsProvider rootComponentsProvider;

    @InjectView(R.id.drawerList)
    protected RecyclerView recyclerView;
    @InjectView(R.id.version)
    protected TextView version;
    protected NavigationDrawerAdapter adapter;
    private NavigationDrawerListener targetDrawerListener;

    private ComponentDescription currentComponent;

    @Override
    protected NavigationDrawerPresenter createPresenter(Bundle savedInstanceState) {
        return new NavigationDrawerPresenter();
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

    private void initUI() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new NavigationDrawerAdapter(new ArrayList<>(this.rootComponentsProvider.getActiveComponents()));
        adapter.setNavigationDrawerCallbacks(this);
        recyclerView.setAdapter(adapter);
        setHeaderIfNeeded();

        try {
            if (BuildConfig.FLAVOR.equals("stage"))
                version.setText(getActivity().getPackageManager()
                        .getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setHeaderIfNeeded() {
        if (ViewUtils.isLandscapeOrientation(getActivity())) return;
        //
        if (adapter.setHeader(createNavigationHeader())) {
            adapter.notifyItemInserted(0);
        } else {
            adapter.notifyItemChanged(0);
        }
    }

    private NavigationHeader createNavigationHeader() {
        return new NavigationHeader(appSessionHolder.get().get().getUser());
    }

    public void onEventMainThread(UpdateUserInfoEvent event) {
        setHeaderIfNeeded();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.targetDrawerListener = null;
    }


    public void show() {
        getView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onNavigationDrawerItemSelected(ComponentDescription newComponent) {
        if (this.targetDrawerListener != null) {
            boolean updateComponentSelection = currentComponent == null ||
                    !newComponent.getKey().equalsIgnoreCase(currentComponent.getKey());

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
        recyclerView.post(() -> {
            currentComponent = newComponent;
            adapter.selectComponent(newComponent);
        });
    }

    @Override
    public void notificationCountChanged(int count) {
        adapter.setNotificationCount(count);
    }
}
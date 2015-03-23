package com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

@Layout(R.layout.fragment_navigation_drawer)
public class NavigationDrawerFragment extends BaseFragment<Presenter> implements Presenter.View, NavigationDrawerListener {

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

    @Inject
    Set<ComponentDescription> componentDescriptionSet;

    List<ComponentDescription> componentDescriptionList;

    private NavigationDrawerListener navigationDrawerListener;
    private NavigationDrawerAdapter adapter;

    private ComponentDescription currentComponent;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.navigationDrawerListener = (NavigationDrawerListener) getActivity();

        ComponentDescription componentDescription;
        if (savedInstanceState != null) {
            componentDescription = this.componentDescriptionList.get(savedInstanceState.getInt(STATE_SELECTED_STATE));
        } else {
            componentDescription = this.componentDescriptionList.get(0);
        }

        selectItem(componentDescription);
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

        this.componentDescriptionList = new ArrayList<>(this.componentDescriptionSet);

        adapter = new NavigationDrawerAdapter(new ArrayList<>(componentDescriptionList), (Injector) getActivity());
        adapter.setNavigationDrawerCallbacks(this);

        if (!ViewUtils.isLandscapeOrientation(getActivity())) {
            adapter.setHeader(getNavigationHeader());
        }

        drawerList.setAdapter(adapter);
        eventBus.register(this);
    }

    private NavigationHeader getNavigationHeader() {
        NavigationHeader navHeader = new NavigationHeader();

        final User user = appSessionHolder.get().get().getUser();

        navHeader.setUserEmail(user.getEmail());
        navHeader.setUserNome(user.getUsername());
        navHeader.setUserCover(Uri.fromFile(new File(user.getCoverPath())));
        navHeader.setUserPhoto(Uri.parse(user.getAvatar().getMedium()));

        return navHeader;
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
            final boolean shouldUpdateComponentSelection = currentComponent == null || !componentDescription.getKey().equalsIgnoreCase(currentComponent.getKey());

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
        outState.putInt(STATE_SELECTED_STATE, componentDescriptionList.indexOf(currentComponent));
    }

    @Override
    public void onNavigationDrawerItemSelected(ComponentDescription componentDescription) {
        selectItem(componentDescription);
    }

    @Override
    public void onNavigationDrawerItemReselected(ComponentDescription componentDescription) {

    }

    public void onBackPressed() {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        if (fm.getBackStackEntryCount() >= 2) {
            final int index = fm.getBackStackEntryCount() - 2;
            final FragmentManager.BackStackEntry backEntry = fm.getBackStackEntryAt(index);
            setCurrentComponent(getComponentDescriptionByKey(backEntry.getName()));
        }
    }

    public void setCurrentComponent(ComponentDescription currentComponent) {
        this.currentComponent = currentComponent;

        if (this.currentComponent != null) {
            final int componentIndex = componentDescriptionList.indexOf(currentComponent);
            adapter.selectPosition(ViewUtils.isLandscapeOrientation(getActivity()) ?
                    componentIndex : componentIndex + 1);

            if (getActivity() != null) {
                getActivity().setTitle(currentComponent.getTitle());
            }
        }
    }

    private ComponentDescription getComponentDescriptionByKey(String key) {
        ComponentDescription component = null;

        for (final ComponentDescription componentDescription : componentDescriptionList) {
            if (componentDescription.getKey().equals(key)) {
                component = componentDescription;
                break;
            }
        }
        return component;
    }
}
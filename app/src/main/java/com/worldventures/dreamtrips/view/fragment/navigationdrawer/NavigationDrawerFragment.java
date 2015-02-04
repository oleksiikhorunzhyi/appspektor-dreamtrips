package com.worldventures.dreamtrips.view.fragment.navigationdrawer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.worldventures.dreamtrips.utils.ViewUtils;
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
    private static final String PREFERENCES_FILE = "my_app_settings"; //TODO: change this to your file
    private static final String STATE_SELECTED_STATE = "selected_navigation_drawer_state";

    @Inject
    @Global
    EventBus eventBus;

    @Inject
    AppSessionHolder appSessionHolder;

    private NavigationDrawerListener navigationDrawerListener;

    @InjectView(R.id.drawerList)
    RecyclerView drawerList;

    private State savedState = State.DREAMTRIPS;
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
            savedState = (State) savedInstanceState.getSerializable(STATE_SELECTED_STATE);
            instanceSaved = true;
        }
        adapter.selectPosition(ViewUtils.isLandscapeOrientation(getActivity()) ?
                savedState.getPosition() : savedState.getPosition() + 1);
        selectItem(savedState);
    }

    @Override
    protected NavigationDrawerPM createPresentationModel(Bundle savedInstanceState) {
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
        navHeader.setUserPhoto(Uri.parse(user.getAvatar().getOriginal()));

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
    public void onDetach() {
        super.onDetach();
        this.navigationDrawerListener = null;
    }

    public List<State> getMenu() {
        return State.getMenuItemsArray();
    }

    void selectItem(State state) {
        if ((!instanceSaved || !state.equals(savedState)) && this.navigationDrawerListener != null) {
            this.navigationDrawerListener.onNavigationDrawerItemSelected(state);
        }
        savedState = state;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_SELECTED_STATE, savedState);
    }

    @Override
    public void onNavigationDrawerItemSelected(State state) {
        selectItem(state);
    }
}
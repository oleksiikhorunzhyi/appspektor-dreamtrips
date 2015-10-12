package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.badoo.mobile.util.WeakHandler;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.innahema.collections.query.functions.Action1;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.friends.bundle.BaseUsersBundle;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.presenter.BaseUserListPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.FriendCell;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;

public abstract class BaseUsersFragment<T extends BaseUserListPresenter, B extends BaseUsersBundle> extends BaseFragmentWithArgs<T, B>
        implements BaseUserListPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.empty)
    protected RelativeLayout emptyView;
    @InjectView(R.id.recyclerViewFriends)
    protected EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    private RecyclerViewStateDelegate stateDelegate;

    protected LoaderRecycleAdapter<User> adapter;

    private WeakHandler weakHandler;
    private LinearLayoutManager layoutManager;

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int itemCount = layoutManager.getItemCount();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            getPresenter().scrolled(itemCount, lastVisibleItemPosition);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stateDelegate.saveStateIfNeeded(outState);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        stateDelegate.setRecyclerView(recyclerView);
        adapter = new LoaderRecycleAdapter<>(getActivity(), this);
        adapter.registerCell(User.class, FriendCell.class);

        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);

        layoutManager = createLayoutManager();
        recyclerView.setLayoutManager(layoutManager);
        refreshLayout.setOnRefreshListener(this);
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
    }

    protected LinearLayoutManager createLayoutManager() {
        return ViewUtils.isLandscapeOrientation(getActivity()) ?
                new GridLayoutManager(getActivity(), ViewUtils.isTablet(getActivity()) ? 3 : 1) :
                new LinearLayoutManager(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.addOnScrollListener(onScrollListener);
    }


    @Override
    public void onPause() {
        super.onPause();
        recyclerView.removeOnScrollListener(onScrollListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stateDelegate.onDestroyView();
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    public void finishLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
        });
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(true);
        });
    }

    @Override
    public void refreshUsers(List<User> users) {
        adapter.setItems(users);
    }

    @Override
    public void openFriendPrefs(UserBundle userBundle) {
        if (isVisibleOnScreen())
            NavigationBuilder.create().with(activityRouter).data(userBundle).move(Route.FRIEND_PREFERENCES);
    }

    @Override
    public void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectedAction) {
        if (isVisibleOnScreen()) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
            builder.title(getString(R.string.profile_add_friend))
                    .adapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, circles),
                            (materialDialog, view, i, charSequence) -> {
                                selectedAction.apply(i);
                                materialDialog.dismiss();
                            })
                    .negativeText(R.string.cancel)
                    .show();
        }
    }

    @Override
    public void openUser(UserBundle userBundle) {
        if (isVisibleOnScreen())
            NavigationBuilder.create().with(activityRouter)
                    .data(userBundle)
                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                    .move(routeCreator.createRoute(userBundle.getUser().getId()));

    }

    @Override
    protected abstract T createPresenter(Bundle savedInstanceState);
}


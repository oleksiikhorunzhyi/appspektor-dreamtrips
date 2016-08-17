package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.badoo.mobile.util.WeakHandler;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.innahema.collections.query.functions.Action1;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
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
    @InjectView(R.id.caption)
    protected TextView caption;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    private RecyclerViewStateDelegate stateDelegate;

    protected LoaderRecycleAdapter<User> adapter;

    protected WeakHandler weakHandler;
    private LinearLayoutManager layoutManager;

    private MaterialDialog blockingProgressDialog;

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
        if (!ViewUtils.isLandscapeOrientation(getActivity())) {
            recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources()
                    .getDrawable(R.drawable.list_divider), true));
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView1, int dx, int dy) {
                checkScrolledItems();
            }
        });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
    }

    protected LinearLayoutManager createLayoutManager() {
        return ViewUtils.isLandscapeOrientation(getActivity()) ?
                new GridLayoutManager(getActivity(), ViewUtils.isTablet(getActivity()) ? 3 : 1) :
                new LinearLayoutManager(getActivity());
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
    public void startLoading() {
        // timeout was set according to the issue:
        // https://code.google.com/p/android/issues/detail?id=77712
        weakHandler.postDelayed(() -> {
            if (refreshLayout != null)
                refreshLayout.setRefreshing(true);
        }, 100);
    }

    @Override
    public void finishLoading() {
        weakHandler.postDelayed(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
        }, 100);
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public void showBlockingProgress() {
        blockingProgressDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .content(R.string.loading)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show();
    }

    @Override
    public void hideBlockingProgress() {
        if (blockingProgressDialog != null) blockingProgressDialog.dismiss();
    }

    @Override
    public void refreshUsers(List<User> users) {
        adapter.setItems(users);
        checkScrolledItems();
    }

    private void checkScrolledItems() {
        int itemCount = layoutManager.getItemCount();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        getPresenter().scrolled(itemCount, lastVisibleItemPosition);
    }

    @Override
    public void openFriendPrefs(UserBundle userBundle) {
        if (isVisibleOnScreen())
            router.moveTo(Route.FRIEND_PREFERENCES, NavigationConfigBuilder.forActivity()
                    .data(userBundle)
                    .build());
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
                    .negativeText(R.string.action_cancel)
                    .show();
        }
    }

    @Override
    public void openUser(UserBundle userBundle) {
        if (isVisibleOnScreen())
            router.moveTo(routeCreator.createRoute(userBundle.getUser().getId()), NavigationConfigBuilder.forActivity()
                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                    .data(userBundle)
                    .build());

    }

    @Override
    protected abstract T createPresenter(Bundle savedInstanceState);
}


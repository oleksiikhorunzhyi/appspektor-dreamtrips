package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedView;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icicle;

@Layout(R.layout.fragment_feed)
public class FeedFragment extends BaseFragment<FeedPresenter>
        implements FeedPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    @InjectView(R.id.fab_post)
    FloatingActionButton fabPost;
    @InjectView(R.id.feedview)
    FeedView feedView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    private WeakHandler weakHandler;
    private Bundle savedInstanceState;

    private BaseArrayListAdapter<BaseEventModel> adapter;

    @Icicle
    ArrayList<BaseEventModel> items;
    @Icicle
    boolean postShown;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (adapter != null) {
            List<BaseEventModel> items = adapter.getItems();
            this.items = new ArrayList<>(items);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        weakHandler = new WeakHandler();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        swipeContainer.setColorSchemeResources(R.color.theme_main_darker);
        swipeContainer.setOnRefreshListener(this);
        adapter = new BaseArrayListAdapter<>(feedView.getContext(), injectorProvider);
        feedView.setup(savedInstanceState, adapter);
        if (items != null) {
            adapter.addItems(items);
        }
        adapter.notifyDataSetChanged();

        feedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int itemCount = feedView.getLayoutManager().getItemCount();
                int lastVisibleItemPosition = feedView.getLayoutManager().findLastVisibleItemPosition();
                getPresenter().scrolled(itemCount, lastVisibleItemPosition);
            }
        });

        if (postShown) openPost();
    }

    @OnClick(R.id.fab_post)
    void onPostClicked() {
        openPost();
    }

    @Override
    public void onRefresh() {
        getPresenter().reloadFeed();
    }

    @Override
    protected FeedPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedPresenter();
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (swipeContainer != null) swipeContainer.setRefreshing(true);
        });
    }

    @Override
    public void finishLoading() {
        weakHandler.post(() -> {
            if (swipeContainer != null) swipeContainer.setRefreshing(false);
        });
    }

    @Override
    public BaseArrayListAdapter<BaseEventModel> getAdapter() {
        return adapter;
    }

    public void openPost() {
        postShown = true;
        showPostContainer();

        fragmentCompass.removePost();
        fragmentCompass.disableBackStack();
        fragmentCompass.setContainerId(R.id.container_details_floating);
        //
        NavigationBuilder.create()
                .with(fragmentCompass)
                .attach(Route.POST_CREATE);
    }

    @Override
    public void setEmptyViewVisibility(boolean visible) {
        //TODO
    }

    private void showPostContainer() {
        View container = ButterKnife.findById(getActivity(), R.id.container_details_floating);
        if (container != null) container.setVisibility(View.VISIBLE);
    }

    @Override
    public void insertItem(BaseEventModel baseEventModel) {
        feedView.getAdapter().addItem(0, baseEventModel);
        feedView.getAdapter().notifyItemInserted(0);
        feedView.scrollToPosition(0);
    }

}

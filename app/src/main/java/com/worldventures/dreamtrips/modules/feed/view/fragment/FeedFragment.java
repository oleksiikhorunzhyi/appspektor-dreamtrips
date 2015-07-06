package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedAvatarEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedCoverEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedTripEventModel;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedAvatarEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedBucketEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedCoverEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPhotoEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedTripEventCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.fragment_feed_fragment)
public class FeedFragment extends BaseFragment<FeedPresenter> implements SwipeRefreshLayout.OnRefreshListener, FeedPresenter.View {

    @Optional
    @InjectView(R.id.empty)
    RelativeLayout emptyView;
    @InjectView(R.id.recyclerViewFeed)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    private RecyclerViewStateDelegate stateDelegate;

    private LoaderRecycleAdapter<BaseFeedModel> adapter;

    private WeakHandler weakHandler;

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
        adapter = new LoaderRecycleAdapter<>(getActivity(), injectorProvider);
        adapter.registerCell(FeedAvatarEventModel.class, FeedAvatarEventCell.class);
        adapter.registerCell(FeedCoverEventModel.class, FeedCoverEventCell.class);

        adapter.registerCell(FeedPhotoEventModel.class, FeedPhotoEventCell.class);
        adapter.registerCell(FeedTripEventModel.class, FeedTripEventCell.class);

        adapter.registerCell(FeedBucketEventModel.class, FeedBucketEventCell.class);

        if (emptyView != null) recyclerView.setEmptyView(emptyView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        weakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addItem(new FeedAvatarEventModel());
                adapter.addItem(new FeedCoverEventModel());
                adapter.addItem(new FeedPhotoEventModel());
                adapter.addItem(new FeedTripEventModel());
                adapter.addItem(new FeedBucketEventModel());
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "SHOW", Toast.LENGTH_SHORT).show();
            }
        }, 3000);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stateDelegate.onDestroyView();
    }

    @Override
    protected FeedPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedPresenter();
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    public IRoboSpiceAdapter<BaseFeedModel> getAdapter() {
        return adapter;
    }

    @Override
    public void finishLoading(List<BaseFeedModel> items) {
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
}

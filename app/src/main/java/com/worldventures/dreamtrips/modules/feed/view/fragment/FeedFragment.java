package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedView;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_feed_fragment)
public class FeedFragment extends BaseFragment<FeedPresenter> implements SwipeRefreshLayout.OnRefreshListener, FeedPresenter.View {

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.feedview)
    FeedView feedView;

    private WeakHandler weakHandler;
    private Bundle savedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        feedView.onSaveInstanceState(outState);

    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        feedView.setup(injectorProvider, savedInstanceState);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
    public IRoboSpiceAdapter<ParentFeedModel> getAdapter() {
        return feedView.getAdapter();
    }

    @Override
    public void finishLoading(List<ParentFeedModel> items) {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
        });
        feedView.restoreStateIfNeeded();
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(true);
        });
    }
}

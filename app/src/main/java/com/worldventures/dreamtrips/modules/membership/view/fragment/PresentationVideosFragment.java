package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.membership.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.modules.video.cell.VideoCell;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.view.BaseVideoFragment;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_presentation_videos)
public class PresentationVideosFragment extends BaseVideoFragment<PresentationVideosPresenter>
        implements PresentationVideosPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.lv_items)
    protected EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.ll_empty_view)
    protected ViewGroup emptyView;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    private LoaderRecycleAdapter<Object> arrayListAdapter;

    RecyclerViewStateDelegate stateDelegate;
    private WeakHandler weakHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
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
        setupLayoutManager(ViewUtils.isLandscapeOrientation(getActivity()));
        this.recyclerView.setEmptyView(emptyView);

        this.arrayListAdapter = new LoaderRecycleAdapter<>(getActivity(), injectorProvider);
        this.arrayListAdapter.registerCell(Video.class, VideoCell.class);

        this.recyclerView.setAdapter(this.arrayListAdapter);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
    }

    @Override
    public void onDestroyView() {
        this.recyclerView.setAdapter(null);
        stateDelegate.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupLayoutManager(ViewUtils.isLandscapeOrientation(getActivity()));
    }

    @Override
    public void onRefresh() {
        getPresenter().getAdapterController().reload();
    }

    @Override
    protected PresentationVideosPresenter createPresenter(Bundle savedInstanceState) {
        return new PresentationVideosPresenter();
    }

    private void setupLayoutManager(boolean landscape) {
        int spanCount = landscape ? 2 : 1;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
        this.recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onDeleteAction(CachedEntity cacheEntity) {
        showDeleteDialog(() -> getPresenter().onDeleteAction(cacheEntity));
    }

    @Override
    public void onCancelCaching(CachedEntity cacheEntity) {
        showCancelDialog(() -> getPresenter().onCancelAction(cacheEntity));
    }

    @Override
    public void notifyItemChanged(CachedEntity videoEntity) {
        arrayListAdapter.notifyDataSetChanged();
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
           if  (refreshLayout != null) refreshLayout.setRefreshing(true);
        });

    }

    @Override
    public void finishLoading() {
        weakHandler.post(() -> {
            if  (refreshLayout != null) refreshLayout.setRefreshing(false);
        });
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public BaseArrayListAdapter getAdapter() {
        return arrayListAdapter;
    }

}

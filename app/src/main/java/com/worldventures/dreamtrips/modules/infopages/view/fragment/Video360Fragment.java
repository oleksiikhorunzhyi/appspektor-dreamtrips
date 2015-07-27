package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ScrollView;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.video.cell.Video360Cell;
import com.worldventures.dreamtrips.modules.video.cell.Video360SmallCell;
import com.worldventures.dreamtrips.modules.video.cell.VideoHeaderCell;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.presenter.Video360Presenter;
import com.worldventures.dreamtrips.modules.video.view.BaseVideoFragment;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.fragment_360_videos)
public class Video360Fragment extends BaseVideoFragment<Video360Presenter> implements Video360Presenter.View {

    @Inject
    @ForActivity
    Provider<Injector> injector;

    @Optional
    @InjectView(R.id.recyclerViewFeatured)
    protected RecyclerView recyclerViewFeatured;

    @Optional
    @InjectView(R.id.recyclerViewRecent)
    protected RecyclerView recyclerViewRecent;

    @Optional
    @InjectView(R.id.recyclerViewAll)
    protected RecyclerView recyclerViewAll;

    @InjectView(R.id.containerLandscape)
    protected ScrollView scrollView;

    @InjectView(R.id.progress) View progress;

    private BaseArrayListAdapter<Video> adapterFeatured;
    private BaseArrayListAdapter<Video> adapterRecent;
    private BaseArrayListAdapter<Object> adapterAll;

    RecyclerViewStateDelegate stateDelegate;

    @Override
    protected Video360Presenter createPresenter(Bundle savedInstanceState) {
        return new Video360Presenter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        adapterFeatured = new BaseArrayListAdapter<>(getActivity(), injector);
        adapterRecent = new BaseArrayListAdapter<>(getActivity(), injector);

        adapterFeatured.registerCell(Video.class, Video360Cell.class);
        adapterRecent.registerCell(Video.class, Video360SmallCell.class);

        adapterAll = new BaseArrayListAdapter<>(getActivity(), injector);
        adapterAll.registerCell(Video.class, Video360Cell.class);
        adapterAll.registerCell(String.class, VideoHeaderCell.class);

        recyclerViewAll.setAdapter(adapterAll);
        recyclerViewFeatured.setAdapter(adapterFeatured);
        recyclerViewRecent.setAdapter(adapterRecent);

        progress.setVisibility(View.VISIBLE);

        setUpRecyclerViews();
    }

    @Override
    public void onDestroyView() {
        stateDelegate.onDestroyView();
        this.recyclerViewAll.setAdapter(null);
        this.recyclerViewFeatured.setAdapter(null);
        this.recyclerViewRecent.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void finishLoading() {
        progress.setVisibility(View.GONE);
        setUpRecyclerViews();
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public BaseArrayListAdapter getFeaturedAdapter() {
        return adapterFeatured;
    }

    @Override
    public BaseArrayListAdapter getRecentAdapter() {
        return adapterRecent;
    }

    @Override
    public BaseArrayListAdapter getAllAdapter() {
        return adapterAll;
    }

    @Override
    public void notifyItemChanged(CachedEntity videoEntity) {
        if (getFeaturedAdapter() != null) {
            getFeaturedAdapter().notifyDataSetChanged();
        }
        if (getRecentAdapter() != null) {
            getRecentAdapter().notifyDataSetChanged();
        }
        if (getAllAdapter() != null) {
            getAllAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onDeleteAction(CachedEntity cacheEntity) {
        showDeleteDialog(() -> getPresenter().onDeleteAction(cacheEntity));
    }

    @Override
    public void onCancelCaching(CachedEntity cacheEntity) {
        showCancelDialog(() -> getPresenter().onCancelAction(cacheEntity));
    }

    private void setUpRecyclerViews() {
        if (ViewUtils.isLandscapeOrientation(getActivity())) {
            recyclerViewAll.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            LinearLayoutManager linearLayoutManagerFeatured = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager linearLayoutManagerRecent = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false);
            recyclerViewFeatured.setLayoutManager(linearLayoutManagerFeatured);
            recyclerViewRecent.setLayoutManager(linearLayoutManagerRecent);

            getPresenter().fillFeatured();

            if (adapterAll != null) {
                adapterAll.clear();
                adapterAll.notifyDataSetChanged();
            }
            stateDelegate.setRecyclerView(recyclerViewFeatured);
        } else {
            recyclerViewAll.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerViewAll.setLayoutManager(linearLayoutManager);

            getPresenter().fillAll();

            if (adapterFeatured != null) {
                adapterFeatured.clear();
                adapterFeatured.notifyDataSetChanged();
                adapterRecent.clear();
                adapterRecent.notifyDataSetChanged();
            }
            stateDelegate.setRecyclerView(recyclerViewAll);
        }
    }

}

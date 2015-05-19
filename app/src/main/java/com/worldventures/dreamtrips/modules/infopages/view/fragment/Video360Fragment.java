package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ScrollView;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.video.cell.Video360Cell;
import com.worldventures.dreamtrips.modules.video.cell.Video360SmallCell;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.presenter.Video360Presenter;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.fragment_360_videos)
public class Video360Fragment extends BaseFragment<Video360Presenter> implements Video360Presenter.View {

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

    private BaseArrayListAdapter<Video> adapterFeatured;
    private BaseArrayListAdapter<Video> adapterRecent;
    private BaseArrayListAdapter<Video> adapterAll;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        adapterFeatured = new BaseArrayListAdapter<>(getActivity(), injector);
        adapterRecent = new BaseArrayListAdapter<>(getActivity(), injector);

        adapterFeatured.registerCell(Video.class, Video360Cell.class);
        adapterRecent.registerCell(Video.class, Video360SmallCell.class);

        adapterAll = new BaseArrayListAdapter<>(getActivity(), injector);
        adapterAll.registerCell(Video.class, Video360Cell.class);

        recyclerViewAll.setAdapter(adapterAll);
        recyclerViewFeatured.setAdapter(adapterFeatured);
        recyclerViewRecent.setAdapter(adapterRecent);

        setUp();
    }

    @Override
    public void onDestroyView() {
        this.recyclerViewAll.setAdapter(null);
        this.recyclerViewFeatured.setAdapter(null);
        this.recyclerViewRecent.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void finishLoading() {
        setUp();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setUp();
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
    protected Video360Presenter createPresenter(Bundle savedInstanceState) {
        return new Video360Presenter();
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
    public void onDeleteAction(CachedEntity videoEntity) {
        getPresenter().onDeleteAction(videoEntity);
    }

    @Override
    public void onCancelCaching(CachedEntity cacheEntity) {
        getPresenter().onCancelAction(cacheEntity);
    }

    private void setUp() {
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
        }
    }

}

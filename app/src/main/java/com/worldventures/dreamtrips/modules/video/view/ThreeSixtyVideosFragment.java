package com.worldventures.dreamtrips.modules.video.view;

import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.membership.model.VideoHeader;
import com.worldventures.dreamtrips.modules.video.cell.Video360Cell;
import com.worldventures.dreamtrips.modules.video.cell.Video360SmallCell;
import com.worldventures.dreamtrips.modules.video.cell.VideoHeaderCell;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.presenter.ThreeSixtyVideosPresenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.fragment_360_videos)
public class ThreeSixtyVideosFragment extends BaseVideoFragment<ThreeSixtyVideosPresenter>
        implements ThreeSixtyVideosPresenter.View, SwipeRefreshLayout.OnRefreshListener {

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
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;

    private BaseArrayListAdapter<Object> adapterFeatured;
    private BaseArrayListAdapter<Object> adapterRecent;
    private BaseArrayListAdapter<Object> adapterAll;

    private WeakHandler weakHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        if (recyclerViewAll != null) {
            adapterAll = new BaseArrayListAdapter<>(getActivity(), injector);
            adapterAll.registerCell(Video.class, Video360Cell.class);
            adapterAll.registerCell(VideoHeader.class, VideoHeaderCell.class);

            recyclerViewAll.setAdapter(adapterAll);
        }

        if (recyclerViewRecent != null) {
            adapterFeatured = new BaseArrayListAdapter<>(getActivity(), injector);
            adapterRecent = new BaseArrayListAdapter<>(getActivity(), injector);

            adapterFeatured.registerCell(Video.class, Video360Cell.class);
            adapterRecent.registerCell(Video.class, Video360SmallCell.class);
            recyclerViewFeatured.setAdapter(adapterFeatured);
            recyclerViewRecent.setAdapter(adapterRecent);
        }

        setUpRecyclerViews();
    }

    @Override
    protected ThreeSixtyVideosPresenter createPresenter(Bundle savedInstanceState) {
        return new ThreeSixtyVideosPresenter();
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(true);
        });

    }

    @Override
    public void finishLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void setItems(List<Object> videos) {
        if (ViewUtils.isLandscapeOrientation(getActivity())) {
            adapterFeatured.setItems(Queryable.from(videos)
                    .filter(element -> element instanceof Video && ((Video) element).isFeatured())
                    .toList());
            adapterRecent.setItems(Queryable.from(videos)
                    .filter(element -> element instanceof Video && ((Video) element).isRecent())
                    .toList());
        } else {
            adapterAll.setItems(videos);
        }
    }

    @Override
    public void notifyItemChanged(CachedEntity videoEntity) {
        if (adapterFeatured != null) {
            adapterFeatured.notifyDataSetChanged();
        }
        if (adapterRecent != null) {
            adapterRecent.notifyDataSetChanged();
        }
        if (adapterAll != null) {
            adapterAll.notifyDataSetChanged();
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
            LinearLayoutManager linearLayoutManagerFeatured = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager linearLayoutManagerRecent = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false);
            recyclerViewFeatured.setLayoutManager(linearLayoutManagerFeatured);
            recyclerViewRecent.setLayoutManager(linearLayoutManagerRecent);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerViewAll.setLayoutManager(linearLayoutManager);
        }
    }
}

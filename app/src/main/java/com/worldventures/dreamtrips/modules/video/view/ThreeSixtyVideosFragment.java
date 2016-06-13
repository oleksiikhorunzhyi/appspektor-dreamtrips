package com.worldventures.dreamtrips.modules.video.view;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.video.cell.MediaHeaderCell;
import com.worldventures.dreamtrips.modules.video.cell.Video360Cell;
import com.worldventures.dreamtrips.modules.video.cell.Video360SmallCell;
import com.worldventures.dreamtrips.modules.video.cell.delegate.VideoCellDelegate;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.presenter.ThreeSixtyVideosPresenter;

import java.util.List;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.fragment_360_videos)
public class ThreeSixtyVideosFragment extends BaseMediaFragment<ThreeSixtyVideosPresenter>
        implements ThreeSixtyVideosPresenter.View, SwipeRefreshLayout.OnRefreshListener, VideoCellDelegate {

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

    private BaseDelegateAdapter<Object> adapterFeatured;
    private BaseDelegateAdapter<Object> adapterRecent;
    private BaseDelegateAdapter<Object> adapterAll;

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
            adapterAll = new BaseDelegateAdapter<>(getActivity(), this);
            adapterAll.registerCell(Video.class, Video360Cell.class);
            adapterAll.registerDelegate(Video.class, this);
            adapterAll.registerCell(MediaHeader.class, MediaHeaderCell.class);

            recyclerViewAll.setAdapter(adapterAll);
        }

        if (recyclerViewRecent != null) {
            adapterFeatured = new BaseDelegateAdapter<>(getActivity(), this);
            adapterRecent = new BaseDelegateAdapter<>(getActivity(), this);

            adapterFeatured.registerCell(Video.class, Video360Cell.class);
            adapterRecent.registerCell(Video.class, Video360SmallCell.class);
            adapterFeatured.registerDelegate(Video.class, this);
            adapterRecent.registerDelegate(Video.class, this);
            recyclerViewFeatured.setAdapter(adapterFeatured);
            recyclerViewRecent.setAdapter(adapterRecent);
        }

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

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
            List featuredObjects = Queryable.from(videos)
                    .filter(element -> element instanceof Video && ((Video) element).isFeatured())
                    .toList();
            if (adapterFeatured != null) adapterFeatured.setItems(featuredObjects);

            List recentObjects = Queryable.from(videos)
                    .filter(element -> element instanceof Video && ((Video) element).isRecent())
                    .toList();
            if (adapterRecent != null) adapterRecent.setItems(recentObjects);
        } else {
            if (adapterAll != null) adapterAll.setItems(videos);
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
        showDialog(R.string.delete_cached_video_title,
                R.string.delete_cached_video_text,
                R.string.delete_photo_positiove,
                R.string.delete_photo_negative,
                () -> getPresenter().onDeleteAction(cacheEntity));
    }

    @Override
    public void onCancelCaching(CachedEntity cacheEntity) {
        showDialog(R.string.cancel_cached_video_title,
                R.string.cancel_cached_video_text,
                R.string.cancel_photo_positiove,
                R.string.cancel_photo_negative,
                () -> getPresenter().onCancelAction(cacheEntity));
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

    @Override
    public void sendAnalytic(String action, String name) {
        TrackingHelper.actionMembershipVideo(action, name);
    }

    @Override
    public void onDownloadVideo(CachedEntity entity) {
        getPresenter().downloadVideo(entity);
    }

    @Override
    public void onDeleteVideo(CachedEntity entity) {
        getPresenter().deleteCachedVideo(entity);
    }

    @Override
    public void onCancelCachingVideo(CachedEntity entity) {
        getPresenter().cancelCachingVideo(entity);
    }

    @Override
    public void onCellClicked(Video model) {

    }
}

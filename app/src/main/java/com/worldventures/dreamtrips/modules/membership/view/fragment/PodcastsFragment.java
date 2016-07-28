package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;
import com.worldventures.dreamtrips.modules.membership.presenter.PodcastsPresenter;
import com.worldventures.dreamtrips.modules.membership.view.cell.PodcastCell;
import com.worldventures.dreamtrips.modules.membership.view.cell.delegate.PodcastCellDelegate;
import com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration;
import com.worldventures.dreamtrips.modules.video.cell.MediaHeaderLightCell;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_podcasts)
public class PodcastsFragment extends RxBaseFragment<PodcastsPresenter> implements PodcastsPresenter.View, SwipeRefreshLayout.OnRefreshListener, PodcastCellDelegate {

    @InjectView(R.id.list_items) EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.empty_view) ViewGroup emptyView;

    private BaseDelegateAdapter<Object> adapter;
    private RecyclerViewStateDelegate stateDelegate;
    private WeakHandler weakHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        stateDelegate.setRecyclerView(recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new BaseDelegateAdapter<>(getContext(), this);
        adapter.setHasStableIds(true);

        adapter.registerCell(Podcast.class, PodcastCell.class);
        adapter.registerDelegate(Podcast.class, this);
        adapter.registerCell(MediaHeader.class, MediaHeaderLightCell.class);

        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int itemCount = recyclerView.getLayoutManager().getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                getPresenter().scrolled(itemCount, lastVisibleItemPosition);
            }
        });

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stateDelegate.saveStateIfNeeded(outState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) getPresenter().track();
    }

    @Override
    public void onDestroyView() {
        recyclerView.setAdapter(null);
        stateDelegate.onDestroyView();
        super.onDestroyView();
    }

    @Override
    protected PodcastsPresenter createPresenter(Bundle savedInstanceState) {
        return new PodcastsPresenter();
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
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public void setItems(List podcasts) {
        adapter.setItems(podcasts);
    }

    @Override
    public void onRefresh() {
        getPresenter().reloadPodcasts();
    }

    @Override
    public void notifyItemChanged(CachedEntity videoEntity) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleteAction(CachedEntity cacheEntity) {
        showDialog(R.string.delete_cached_podcast_title,
                R.string.delete_cached_podcast_text,
                R.string.delete_photo_positiove,
                R.string.delete_photo_negative,
                (dialog, which) -> getPresenter().onDeleteAction(cacheEntity));
    }

    @Override
    public void onCancelCaching(CachedEntity cacheEntity) {
        showDialog(R.string.cancel_cached_podcast_title,
                R.string.cancel_cached_podcast_text,
                R.string.cancel_photo_positiove,
                R.string.cancel_photo_negative,
                (dialog, which) -> getPresenter().onCancelAction(cacheEntity));
    }

    @Override
    public void onCellClicked(Podcast model) {
    }

    @Override
    public void onDownloadPodcast(CachedEntity entity) {
        getPresenter().downloadPodcast(entity);
    }

    @Override
    public void onDeletePodcast(CachedEntity entity) {
        getPresenter().deleteCachedPodcast(entity);
    }

    @Override
    public void onCancelCachingPodcast(CachedEntity entity) {
        getPresenter().cancelCachingPodcast(entity);
    }

    @Override
    public void play(Podcast podcast) {
        getPresenter().play(podcast);
    }

    private void showDialog(@StringRes int title, @StringRes int content,
                            @StringRes int positive, @StringRes int negative,
                            MaterialDialog.SingleButtonCallback callback) {
        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(content)
                .positiveText(positive)
                .negativeText(negative)
                .onPositive(callback)
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        return false;
    }

    @Override
    public void onApiCallFailed() {
    }
}

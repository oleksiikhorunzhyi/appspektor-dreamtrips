package com.worldventures.dreamtrips.social.ui.membership.view.fragment;

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
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.DividerItemDecoration;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.custom.EmptyRecyclerView;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;
import com.worldventures.dreamtrips.social.ui.membership.presenter.PodcastsPresenter;
import com.worldventures.dreamtrips.social.ui.membership.view.cell.PodcastCell;
import com.worldventures.dreamtrips.social.ui.membership.view.cell.delegate.PodcastCellDelegate;
import com.worldventures.dreamtrips.social.ui.video.cell.MediaHeaderLightCell;

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
   protected void trackViewFromViewPagerIfNeeded() {
      getPresenter().track();
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
      getPresenter().onRefresh();
   }

   @Override
   public void notifyItemChanged(CachedModel videoEntity) {
      adapter.notifyDataSetChanged();
   }

   @Override
   public void onDeleteAction(CachedModel cacheEntity) {
      showDialog(R.string.delete_cached_podcast_title, R.string.delete_cached_podcast_text, R.string.delete_photo_positiove, R.string.delete_photo_negative, (dialog, which) -> getPresenter()
            .onDeleteAction(cacheEntity));
   }

   @Override
   public void onCancelCaching(CachedModel cacheEntity) {
      showDialog(R.string.cancel_cached_podcast_title, R.string.cancel_cached_podcast_text, R.string.cancel_photo_positiove, R.string.cancel_photo_negative, (dialog, which) -> getPresenter()
            .onCancelAction(cacheEntity));
   }

   @Override
   public void onCellClicked(Podcast model) {
   }

   @Override
   public void onDownloadPodcast(CachedModel entity) {
      getPresenter().downloadPodcast(entity);
   }

   @Override
   public void onDeletePodcast(CachedModel entity) {
      getPresenter().deleteCachedPodcast(entity);
   }

   @Override
   public void onCancelCachingPodcast(CachedModel entity) {
      getPresenter().cancelCachingPodcast(entity);
   }

   @Override
   public void play(Podcast podcast) {
      getPresenter().play(podcast);
   }

   private void showDialog(@StringRes int title, @StringRes int content, @StringRes int positive, @StringRes int negative, MaterialDialog.SingleButtonCallback callback) {
      new MaterialDialog.Builder(getActivity()).title(title)
            .content(content)
            .positiveText(positive)
            .negativeText(negative)
            .onPositive(callback)
            .onNegative((dialog, which) -> dialog.dismiss())
            .show();
   }
}

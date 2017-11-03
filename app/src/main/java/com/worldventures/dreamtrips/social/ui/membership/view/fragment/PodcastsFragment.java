package com.worldventures.dreamtrips.social.ui.membership.view.fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.DividerItemDecoration;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;
import com.worldventures.dreamtrips.social.ui.membership.presenter.PodcastsPresenter;
import com.worldventures.dreamtrips.social.ui.membership.view.cell.PodcastCell;
import com.worldventures.dreamtrips.social.ui.membership.view.cell.delegate.PodcastCellDelegate;
import com.worldventures.dreamtrips.social.ui.video.cell.MediaHeaderLightCell;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Layout(R.layout.fragment_podcasts)
public class PodcastsFragment extends RxBaseFragment<PodcastsPresenter> implements PodcastsPresenter.View, PodcastCellDelegate {

   private BaseDelegateAdapter<Object> adapter;
   private RecyclerViewStateDelegate stateDelegate;
   private StatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;
   private Bundle savedInstanceState;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.savedInstanceState = savedInstanceState;
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      adapter = new BaseDelegateAdapter<>(getContext(), this);
      adapter.registerCell(Podcast.class, PodcastCell.class);
      adapter.registerDelegate(Podcast.class, this);
      adapter.registerCell(MediaHeader.class, MediaHeaderLightCell.class);

      final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

      statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(rootView);
      statePaginatedRecyclerViewManager.init(adapter, savedInstanceState, linearLayoutManager);
      statePaginatedRecyclerViewManager.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
      statePaginatedRecyclerViewManager.setPaginationListener(getPresenter()::onRefresh);
      statePaginatedRecyclerViewManager.setPaginationListener(getPresenter()::onLoadNextPage);

      stateDelegate = new RecyclerViewStateDelegate();
      stateDelegate.onCreate(savedInstanceState);
      stateDelegate.setRecyclerView(statePaginatedRecyclerViewManager.stateRecyclerView);
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
      stateDelegate.onDestroyView();
      super.onDestroyView();
   }

   @Override
   protected PodcastsPresenter createPresenter(Bundle savedInstanceState) {
      return new PodcastsPresenter();
   }

   @Override
   public void startLoading() {
      statePaginatedRecyclerViewManager.startLoading();
   }

   @Override
   public void finishLoading(boolean noMoreElements) {
      statePaginatedRecyclerViewManager.finishLoading();
      statePaginatedRecyclerViewManager.updateLoadingStatus(false, noMoreElements);
      stateDelegate.restoreStateIfNeeded();
   }

   @Override
   public void setItems(@NotNull List podcasts) {
      adapter.setItems(new ArrayList<Object>(podcasts));
   }

   @Override
   public void notifyItemChanged(CachedModel videoEntity) {
      int position = adapter.getItems().indexOf(videoEntity);
      if (position != -1) adapter.notifyItemChanged(position);
   }

   @Override
   public void showDeleteDialog(CachedModel cacheEntity) {
      showDialog(R.string.delete_cached_podcast_title, R.string.delete_cached_podcast_text, R.string.delete_photo_positiove, R.string.delete_photo_negative, (dialog, which) -> getPresenter()
            .onDeletePodcastAccepted(cacheEntity));
   }

   @Override
   public void onCancelDialog(CachedModel cacheEntity) {
      showDialog(R.string.cancel_cached_podcast_title, R.string.cancel_cached_podcast_text, R.string.cancel_photo_positiove, R.string.cancel_photo_negative, (dialog, which) -> getPresenter()
            .onCancelPodcastAccepted(cacheEntity));
   }

   @Override
   public void onCellClicked(Podcast model) {
   }

   @Override
   public void onDownloadPodcast(CachedModel entity) {
      getPresenter().onDownloadPodcastRequired(entity);
   }

   @Override
   public void onDeletePodcast(CachedModel entity) {
      getPresenter().onDeletePodcastRequired(entity);
   }

   @Override
   public void onCancelCachingPodcast(CachedModel entity) {
      getPresenter().onCancelPodcastRequired(entity);
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

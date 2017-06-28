package com.worldventures.dreamtrips.modules.video.view;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.video.cell.MediaHeaderLightCell;
import com.worldventures.dreamtrips.modules.video.cell.VideoCell;
import com.worldventures.dreamtrips.modules.video.cell.delegate.VideoCellDelegate;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.presenter.PresentationVideosPresenter;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_presentation_videos)
public class PresentationVideosFragment<T extends PresentationVideosPresenter> extends BaseMediaFragment<T> implements PresentationVideosPresenter.View, SwipeRefreshLayout.OnRefreshListener, VideoCellDelegate {

   @InjectView(R.id.lv_items) protected EmptyRecyclerView recyclerView;
   @InjectView(R.id.swipe_container) protected SwipeRefreshLayout refreshLayout;
   @InjectView(R.id.ll_empty_view) protected ViewGroup emptyView;

   protected BaseDelegateAdapter<Object> adapter;

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
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      stateDelegate.saveStateIfNeeded(outState);
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      stateDelegate.setRecyclerView(recyclerView);
      setupLayoutManager();
      recyclerView.setEmptyView(emptyView);

      adapter = new BaseDelegateAdapter<>(getActivity(), this);
      adapter.registerCell(Video.class, VideoCell.class);
      adapter.registerDelegate(Video.class, this);
      adapter.registerCell(MediaHeader.class, MediaHeaderLightCell.class);

      recyclerView.setAdapter(adapter);

      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
   }

   @Override
   public void onDestroyView() {
      this.recyclerView.setAdapter(null);
      stateDelegate.onDestroyView();
      super.onDestroyView();
   }

   @Override
   protected void trackViewFromViewPagerIfNeeded() {
      getPresenter().track();
   }

   @Override
   public void onRefresh() {
      getPresenter().reload();
   }

   @Override
   protected T createPresenter(Bundle savedInstanceState) {
      return (T) new PresentationVideosPresenter();
   }

   private void setupLayoutManager() {
      boolean landscape = ViewUtils.isLandscapeOrientation(getActivity());
      boolean tablet = ViewUtils.isTablet(getActivity());
      int spanCount = landscape && tablet ? 3 : landscape || tablet ? 2 : 1;
      GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
      layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
         @Override
         public int getSpanSize(int position) {
            return adapter.getItem(position) instanceof MediaHeader ? spanCount : 1;
         }
      });
      this.recyclerView.setLayoutManager(layoutManager);
   }

   @Override
   public void onDeleteAction(CachedModel cacheEntity) {
      showDialog(R.string.delete_cached_video_title, R.string.delete_cached_video_text, R.string.delete_photo_positiove, R.string.delete_photo_negative, () -> getPresenter()
            .onDeleteAction(cacheEntity));
   }

   @Override
   public void onCancelCaching(CachedModel cacheEntity) {
      showDialog(R.string.cancel_cached_video_title, R.string.cancel_cached_video_text, R.string.cancel_photo_positiove, R.string.cancel_photo_negative, () -> getPresenter()
            .onCancelAction(cacheEntity));
   }

   @Override
   public void notifyItemChanged(CachedModel videoEntity) {
      adapter.notifyDataSetChanged();
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
   public void setItems(List<Object> videos) {
      adapter.setItems(videos);
   }

   @Override
   public void sendAnalytic(String action, String name) {
      getPresenter().sendAnalytic(action, name);
   }

   @Override
   public void onDownloadVideo(CachedModel entity) {
      getPresenter().downloadVideo(entity);
   }

   @Override
   public void onDeleteVideo(CachedModel entity) {
      getPresenter().deleteCachedVideo(entity);
   }

   @Override
   public void onCancelCachingVideo(CachedModel entity) {
      getPresenter().cancelCachingVideo(entity);
   }

   @Override
   public void onPlayVideoClicked(Video entity) {
      getPresenter().onPlayVideo(entity);
   }

   @Override
   public void onCellClicked(Video model) {
      // nothing to do
   }
}

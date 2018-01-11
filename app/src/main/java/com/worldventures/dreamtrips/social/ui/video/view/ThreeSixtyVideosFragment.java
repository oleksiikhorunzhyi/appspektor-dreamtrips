package com.worldventures.dreamtrips.social.ui.video.view;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldventures.core.modules.video.model.Video;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader;
import com.worldventures.dreamtrips.social.ui.video.cell.MediaHeaderCell;
import com.worldventures.dreamtrips.social.ui.video.cell.Video360Cell;
import com.worldventures.dreamtrips.social.ui.video.cell.Video360SmallCell;
import com.worldventures.dreamtrips.social.ui.video.cell.delegate.Video360CellDelegate;
import com.worldventures.dreamtrips.social.ui.video.presenter.ThreeSixtyVideosPresenter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.fragment_360_videos)
public class ThreeSixtyVideosFragment extends VideoBaseFragment<ThreeSixtyVideosPresenter> implements
      ThreeSixtyVideosPresenter.View, Video360CellDelegate, SelectablePagerFragment {

   @Optional @InjectView(R.id.recyclerViewFeatured) protected RecyclerView recyclerViewFeatured;
   @Optional @InjectView(R.id.recyclerViewRecent) protected RecyclerView recyclerViewRecent;
   @Optional @InjectView(R.id.recyclerViewAll) protected RecyclerView recyclerViewAll;
   @InjectView(R.id.swipe_container) protected SwipeRefreshLayout refreshLayout;

   private BaseDelegateAdapter<Object> adapterFeatured;
   private BaseDelegateAdapter<Object> adapterRecent;
   private BaseDelegateAdapter<Object> adapterAll;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      adapterAll = new BaseDelegateAdapter<>(getActivity(), this);
      adapterAll.registerCell(Video.class, Video360Cell.class);
      adapterAll.registerDelegate(Video.class, this);
      adapterAll.registerCell(MediaHeader.class, MediaHeaderCell.class);
      recyclerViewAll.setAdapter(adapterAll);
      recyclerViewAll.setLayoutManager(new LinearLayoutManager(getActivity()));

      adapterRecent = new BaseDelegateAdapter<>(getActivity(), this);
      adapterRecent.registerCell(Video.class, Video360SmallCell.class);
      adapterRecent.registerDelegate(Video.class, this);
      recyclerViewRecent.setAdapter(adapterRecent);
      recyclerViewRecent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

      adapterFeatured = new BaseDelegateAdapter<>(getActivity(), this);
      adapterFeatured.registerCell(Video.class, Video360Cell.class);
      adapterFeatured.registerCell(Video.class, Video360Cell.class);
      adapterFeatured.registerDelegate(Video.class, this);
      recyclerViewFeatured.setAdapter(adapterFeatured);
      recyclerViewFeatured.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
   }

   @Override
   public void onSelectedFromPager() {
      getPresenter().onSelected();
   }

   @Override
   protected ThreeSixtyVideosPresenter createPresenter(Bundle savedInstanceState) {
      return new ThreeSixtyVideosPresenter();
   }

   @Override
   public void setItems(@Nullable ArrayList<Object> allVideos, @Nullable ArrayList<Object> featuredVideos, @Nullable ArrayList<Object> recentVideos) {
      if (allVideos != null) {
         adapterAll.setItems(allVideos);
      }
      if (featuredVideos != null) {
         adapterFeatured.setItems(featuredVideos);
      }
      if (recentVideos != null) {
         adapterRecent.setItems(recentVideos);
      }
   }

   @Override
   public void notifyItemChanged(@NotNull String uuid) {
      if (adapterAll != null) {
         adapterAll.notifyDataSetChanged();
      }
      if (adapterRecent != null) {
         adapterRecent.notifyDataSetChanged();
      }
      if (adapterFeatured != null) {
         adapterFeatured.notifyDataSetChanged();
      }
   }

   @Override
   public void onOpen360Video(@NotNull Video video) {
      getPresenter().openVideo360Required(video);
   }
}

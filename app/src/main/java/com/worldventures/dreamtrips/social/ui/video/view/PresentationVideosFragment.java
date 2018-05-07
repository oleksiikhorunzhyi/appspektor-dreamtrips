package com.worldventures.dreamtrips.social.ui.video.view;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.core.modules.video.model.Video;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.custom.EmptyRecyclerView;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader;
import com.worldventures.dreamtrips.social.ui.video.cell.MediaHeaderLightCell;
import com.worldventures.dreamtrips.social.ui.video.cell.VideoCell;
import com.worldventures.dreamtrips.social.ui.video.presenter.PresentationVideosPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

import static com.worldventures.dreamtrips.social.ui.video.view.util.VideoViewFunctionsKt.provideGridLayoutManager;

@Layout(R.layout.fragment_presentation_videos)
public class PresentationVideosFragment extends VideoBaseFragment<PresentationVideosPresenter>
      implements PresentationVideosPresenter.View {

   @InjectView(R.id.lv_items) protected EmptyRecyclerView recyclerView;
   @InjectView(R.id.ll_empty_view) protected ViewGroup emptyView;

   protected BaseDelegateAdapter<Object> adapter;
   private RecyclerViewStateDelegate stateDelegate;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      stateDelegate = new RecyclerViewStateDelegate();
      stateDelegate.onCreate(savedInstanceState);
   }

   @Override
   protected PresentationVideosPresenter createPresenter(Bundle savedInstanceState) {
      return new PresentationVideosPresenter();
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

      adapter = new BaseDelegateAdapter<>(getActivity(), this);
      adapter.registerCell(Video.class, VideoCell.class);
      adapter.registerDelegate(Video.class, this);
      adapter.registerCell(MediaHeader.class, MediaHeaderLightCell.class);

      recyclerView.setAdapter(adapter);
      recyclerView.setLayoutManager(provideGridLayoutManager(getContext(), adapter));
      recyclerView.setEmptyView(emptyView);

      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
   }

   @Override
   public void setItems(@NotNull List<?> videos) {
      adapter.setItems(new ArrayList<>(videos));
   }

   @Override
   public void notifyItemChanged(@NotNull String uuid) {
      adapter.notifyDataSetChanged();
   }

   @Override
   public void finishLoading() {
      super.finishLoading();
      stateDelegate.restoreStateIfNeeded();
   }

   @Override
   protected void trackViewFromViewPagerIfNeeded() {
      getPresenter().trackView();
   }
}
